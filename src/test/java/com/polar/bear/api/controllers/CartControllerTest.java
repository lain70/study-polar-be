package com.polar.bear.api.controllers;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Optional;
import com.polar.bear.api.models.CartRequestVo;
import com.polar.bear.api.models.UserInfoDto;
import com.polar.bear.api.redis.LoginRedisVo;
import com.polar.bear.api.service.CartService;
import com.polar.bear.api.service.UserInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.util.ReflectionTestUtils;

class CartControllerTest {
	private final CartService cart = mock(CartService.class);
	private final UserInfoService users = mock(UserInfoService.class);
	private final CartController controller = new CartController(cart, users);

	@Test
	void anonymousAndNonMemberRequestsNeverReachCart() {
		ReflectionTestUtils.setField(controller, "originServiceKey", "test-key");
		assertThat(controller.getItems(null, "test-key").getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
		LoginRedisVo login = mock(LoginRedisVo.class);
		when(login.getType()).thenReturn("ADMIN");
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("key", "token");
		authentication.setDetails(Optional.of(login));
		assertThat(controller.getItems(authentication, "test-key").getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
		verifyNoInteractions(cart, users);
	}

	@Test
	void memberIdentityComesFromValidatedSessionAndCurrentDatabaseRecord() throws Exception {
		ReflectionTestUtils.setField(controller, "originServiceKey", "test-key");
		LoginRedisVo login = mock(LoginRedisVo.class);
		when(login.getType()).thenReturn("USER");
		when(login.getUserId()).thenReturn("member");
		UserInfoDto user = new UserInfoDto();
		user.setUserNo(11);
		when(users.selectUserInfoById("member")).thenReturn(user);
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("key", "token");
		authentication.setDetails(Optional.of(login));
		CartRequestVo request = new CartRequestVo();
		request.setQuantity(2);
		assertThat(controller.addItem(authentication, "test-key", 7L, request).getStatusCode()).isEqualTo(HttpStatus.OK);
		verify(cart).addItem(11, 7L, 2);
	}

	@Test
	void invalidServiceKeyCannotAccessCart() {
		ReflectionTestUtils.setField(controller, "originServiceKey", "test-key");
		assertThat(controller.getItems(null, "other").getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		verifyNoInteractions(cart, users);
	}
}
