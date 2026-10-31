package com.polar.bear.api.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.UUID;
import javax.sql.DataSource;
import com.polar.bear.api.jwt.JwtUtil;
import com.polar.bear.api.models.UserInfoDto;
import com.polar.bear.api.models.CartItemDto;
import com.polar.bear.api.redis.LoginRedisVo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@SpringBootTest
@ActiveProfiles("loc")
@AutoConfigureMockMvc
@Transactional
@EnabledIfEnvironmentVariable(named = "RUN_CART_INTEGRATION", matches = "true")
class CartIntegrationTest {
	@Autowired private CartService cart;
	@Autowired private UserInfoService users;
	@Autowired private JdbcTemplate jdbc;
	@Autowired private DataSource dataSource;
	@Autowired private org.mybatis.spring.SqlSessionTemplate sqlSession;
	@Autowired private MockMvc mvc;
	@MockBean private JwtUtil jwt;
	@Value("${service-key}") private String serviceKey;

	@Test
	void realDatabaseAndSecurityFilterKeepMembersIsolated() throws Exception {
		try (java.sql.Connection connection = dataSource.getConnection()) {
			assertThat(connection.getMetaData().getURL()).contains("127.0.0.1:3306/");
		}
		Long goodsNo = jdbc.queryForObject("SELECT MIN(GOODS_NO) FROM goods_info WHERE USE_YN='Y' AND DISPLAY_YN='Y' AND GOODS_STATUS='ON_SALE' AND STOCK_QUANTITY>=5 AND (DISPLAY_START_DATE IS NULL OR DISPLAY_START_DATE<=NOW()) AND (DISPLAY_END_DATE IS NULL OR DISPLAY_END_DATE>=NOW())", Long.class);
		assertThat(goodsNo).as("판매중이며 재고가 5개 이상인 로컬 샘플 상품").isNotNull();
		UserInfoDto first = member();
		UserInfoDto second = member();
		session("first-token", first, "USER");
		session("second-token", second, "USER");
		session("admin-token", first, "ADMIN");
		mvc.perform(get("/api/user/cart").header("x-auth-user-service-key", serviceKey)).andExpect(status().isUnauthorized());
		mvc.perform(get("/api/user/cart").header("Authorization", "admin-token").header("x-auth-user-service-key", serviceKey)).andExpect(status().isUnauthorized());
		mvc.perform(get("/api/user/info").param("userNo", second.getUserNo().toString())
				.header("Authorization", "first-token").header("x-auth-user-service-key", serviceKey))
				.andExpect(status().isOk()).andExpect(jsonPath("$.userId").value(first.getUserId()))
				.andExpect(jsonPath("$.userPwd").doesNotExist()).andExpect(jsonPath("$.userNo").doesNotExist())
				.andExpect(header().string("Cache-Control", "no-store"));
		mvc.perform(post("/api/user/cart/{goodsNo}", goodsNo).header("Authorization", "first-token")
				.header("x-auth-user-service-key", serviceKey).contentType(MediaType.APPLICATION_JSON)
				.content("{\"quantity\":2,\"userNo\":" + second.getUserNo() + ",\"unitPrice\":1}"))
				.andExpect(status().isOk());
		assertThat(cart.getItems(second.getUserNo())).isEmpty();
		cart.addItem(first.getUserNo(), goodsNo, 1);
		CartItemDto item = cart.getItems(first.getUserNo()).get(0);
		assertThat(item.getQuantity()).isEqualTo(3);
		assertThat(item.isAvailable()).isTrue();
		assertThat(item.getUnitPrice()).isEqualByComparingTo(jdbc.queryForObject("SELECT COALESCE(DISCOUNT_PRICE, SALE_PRICE) FROM goods_info WHERE GOODS_NO=?", java.math.BigDecimal.class, goodsNo));
		mvc.perform(put("/api/user/cart/{goodsNo}", goodsNo).header("Authorization", "second-token")
				.header("x-auth-user-service-key", serviceKey).contentType(MediaType.APPLICATION_JSON).content("{\"quantity\":1}"))
				.andExpect(status().isNotFound());
		cart.deleteItem(second.getUserNo(), goodsNo);
		assertThat(cart.getItems(first.getUserNo()).get(0).getQuantity()).isEqualTo(3);
		for (String quantity : new String[] {"0", "-1", "null", "2147483648"}) {
			mvc.perform(post("/api/user/cart/{goodsNo}", goodsNo).header("Authorization", "first-token")
					.header("x-auth-user-service-key", serviceKey).contentType(MediaType.APPLICATION_JSON)
					.content("{\"quantity\":" + quantity + "}"))
					.andExpect(status().isBadRequest());
		}
		assertThatThrownBy(() -> cart.addItem(first.getUserNo(), goodsNo, Integer.MAX_VALUE)).isInstanceOf(ResponseStatusException.class);
		// Failed transactional service calls mark the surrounding test transaction rollback-only; it is intentionally rolled back.
		cart.updateQuantity(first.getUserNo(), goodsNo, 1);
		assertThat(cart.getItems(first.getUserNo()).get(0).getQuantity()).isEqualTo(1);
		jdbc.update("UPDATE goods_info SET DISPLAY_YN='N' WHERE GOODS_NO=?", goodsNo);
		sqlSession.clearCache();
		CartItemDto hidden = cart.getItems(first.getUserNo()).get(0);
		assertThat(hidden.isAvailable()).isFalse();
		assertThat(hidden.getUnitPrice()).isNull();
		assertThat(hidden.getGoodsNameKo()).isEqualTo("현재 구매할 수 없는 상품");
		cart.deleteItem(first.getUserNo(), goodsNo);
		assertThat(cart.getItems(first.getUserNo())).isEmpty();
	}

	private UserInfoDto member() throws Exception {
		UserInfoDto user = new UserInfoDto();
		user.setUserId("cart_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16));
		user.setUserName("cart verification");
		user.setUserPwd(UUID.randomUUID().toString());
		users.insertUserInfo(user);
		return user;
	}

	private void session(String token, UserInfoDto user, String type) throws Exception {
		LoginRedisVo login = LoginRedisVo.builder().userKey(user.getUserId()).userId(user.getUserId()).type(type).userInfoDto(user).build();
		when(jwt.validateAccessToken(token)).thenReturn(login);
	}
}
