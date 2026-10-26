package com.polar.bear.api.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import com.polar.bear.api.jwt.JwtUtil;
import com.polar.bear.api.models.GoodsInfoDto;
import com.polar.bear.api.redis.LoginRedisVo;
import com.polar.bear.api.service.GoodsInfoService;
import com.polar.bear.api.service.GoodsUpdateService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

class AdminGoodsUpdateControllerTest {
	private final GoodsInfoService persistence = mock(GoodsInfoService.class);
	private final GoodsUpdateService updates = mock(GoodsUpdateService.class);
	private final JwtUtil jwt = mock(JwtUtil.class);
	private final AdminGoodsController controller = new AdminGoodsController(persistence, updates, jwt);

	@Test
	void localAcceptsAndProductionCompletesWithTrustedIdentity() throws Exception {
		ReflectionTestUtils.setField(controller, "originServiceKey", "test-service-key");
		LoginRedisVo login = mock(LoginRedisVo.class);
		when(login.getUserId()).thenReturn("test-admin");
		when(jwt.validateAccessToken("test-token")).thenReturn(login);
		when(persistence.selectGoodsInfo(7L)).thenReturn(validGoods());
		for (boolean queued : new boolean[] {true, false}) {
			when(updates.update(any())).thenReturn(queued);
			GoodsInfoDto goods = validGoods();
			goods.setGoodsNo(999L);
			goods.setUpdtId("untrusted");
			assertThat(controller.updateGoods("test-token", "test-service-key", 7L, goods).getStatusCode())
					.isEqualTo(queued ? HttpStatus.ACCEPTED : HttpStatus.OK);
			assertThat(goods.getGoodsNo()).isEqualTo(7L);
			assertThat(goods.getUpdtId()).isEqualTo("test-admin");
		}
	}

	@Test
	void missingGoodsAndInvalidInputNeverReachQueue() throws Exception {
		ReflectionTestUtils.setField(controller, "originServiceKey", "test-service-key");
		assertThat(controller.updateGoods("test-token", "test-service-key", 7L,
				new GoodsInfoDto()).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		when(jwt.validateAccessToken("test-token")).thenReturn(mock(LoginRedisVo.class));
		assertThat(controller.updateGoods("test-token", "test-service-key", 7L,
				validGoods()).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		verifyNoInteractions(updates);
	}

	@Test
	void invalidRequiredUpdateFieldsAreRejectedBeforeDatabaseOrQueueAccess() {
		ReflectionTestUtils.setField(controller, "originServiceKey", "test-service-key");
		java.util.List<java.util.function.Consumer<GoodsInfoDto>> invalidValues = java.util.Arrays.asList(
				goods -> goods.setGoodsInfoHistoryNo(null),
				goods -> goods.setGoodsInfoHistoryNo(-1L),
				goods -> goods.setGoodsInfoHistoryNo(0L),
				goods -> goods.setTaxType(null),
				goods -> goods.setTaxType(" "),
				goods -> goods.setShippingType(null),
				goods -> goods.setShippingType(" "),
				goods -> goods.setShippingFee(null),
				goods -> goods.setShippingFee(BigDecimal.valueOf(-1)));
		for (java.util.function.Consumer<GoodsInfoDto> invalidValue : invalidValues) {
			GoodsInfoDto goods = validGoods();
			invalidValue.accept(goods);
			assertThat(controller.updateGoods("test-token", "test-service-key", 7L, goods).getStatusCode())
					.isEqualTo(HttpStatus.BAD_REQUEST);
		}
		verifyNoInteractions(persistence, updates, jwt);
	}

	@Test
	void staleHistoryNoIsRejectedBeforePublishing() throws Exception {
		ReflectionTestUtils.setField(controller, "originServiceKey", "test-service-key");
		when(jwt.validateAccessToken("test-token")).thenReturn(mock(LoginRedisVo.class));
		GoodsInfoDto current = validGoods();
		current.setGoodsInfoHistoryNo(11L);
		when(persistence.selectGoodsInfo(7L)).thenReturn(current);
		assertThat(controller.updateGoods("test-token", "test-service-key", 7L, validGoods()).getStatusCode())
				.isEqualTo(HttpStatus.CONFLICT);
		verifyNoInteractions(updates);
	}

	@Test
	void conflictDetectedDuringDirectSaveReturns409() throws Exception {
		ReflectionTestUtils.setField(controller, "originServiceKey", "test-service-key");
		LoginRedisVo login = mock(LoginRedisVo.class);
		when(login.getUserId()).thenReturn("test-admin");
		when(jwt.validateAccessToken("test-token")).thenReturn(login);
		when(persistence.selectGoodsInfo(7L)).thenReturn(validGoods());
		when(updates.update(any())).thenThrow(new org.springframework.dao.OptimisticLockingFailureException("changed"));
		assertThat(controller.updateGoods("test-token", "test-service-key", 7L, validGoods()).getStatusCode())
				.isEqualTo(HttpStatus.CONFLICT);
	}

	@Test
	void storageLimitViolationsAreRejectedBeforeQueueOrDatabaseAccess() {
		ReflectionTestUtils.setField(controller, "originServiceKey", "test-service-key");
		java.util.List<java.util.function.Consumer<GoodsInfoDto>> invalidValues = java.util.Arrays.asList(
				goods -> goods.setGoodsCode("x".repeat(51)),
				goods -> goods.setGoodsNameKo("x".repeat(201)),
				goods -> goods.setGoodsNameEn("x".repeat(201)),
				goods -> goods.setSellerName("x".repeat(101)),
				goods -> goods.setManufacturer("x".repeat(101)),
				goods -> goods.setOriginCountry("x".repeat(101)),
				goods -> goods.setTaxType("x".repeat(21)),
				goods -> goods.setShippingType("x".repeat(21)),
				goods -> goods.setGoodsStatus("x".repeat(31)),
				goods -> goods.setUseYn("YES"),
				goods -> goods.setDisplayYn("YES"),
				goods -> goods.setShortDescription("x".repeat(501)),
				goods -> goods.setSearchKeywords("x".repeat(501)),
				goods -> goods.setDetailDescription("가".repeat(21846)),
				goods -> goods.setCostPrice(new BigDecimal("10000000000000.00")),
				goods -> goods.setSalePrice(new BigDecimal("10000000000000.00")),
				goods -> goods.setDiscountPrice(new BigDecimal("10000000000000.00")),
				goods -> goods.setShippingFee(new BigDecimal("10000000000000.00")));
		for (java.util.function.Consumer<GoodsInfoDto> invalidValue : invalidValues) {
			GoodsInfoDto goods = validGoods();
			invalidValue.accept(goods);
			assertThat(controller.updateGoods("test-token", "test-service-key", 7L, goods).getStatusCode())
					.isEqualTo(HttpStatus.BAD_REQUEST);
			assertThat(controller.createGoods("test-token", "test-service-key", goods, null).getStatusCode())
					.isEqualTo(HttpStatus.BAD_REQUEST);
		}
		verifyNoInteractions(persistence, updates, jwt);
	}

	@Test
	void exactDatabaseLimitsAndSupplementaryUnicodeCharactersAreAccepted() throws Exception {
		ReflectionTestUtils.setField(controller, "originServiceKey", "test-service-key");
		LoginRedisVo login = mock(LoginRedisVo.class);
		when(login.getUserId()).thenReturn("test-admin");
		when(jwt.validateAccessToken("test-token")).thenReturn(login);
		when(persistence.selectGoodsInfo(7L)).thenReturn(validGoods());
		when(updates.update(any())).thenReturn(true);
		GoodsInfoDto goods = validGoods();
		goods.setGoodsCode("x".repeat(50));
		goods.setGoodsNameKo("🐻".repeat(200));
		goods.setGoodsNameEn("x".repeat(200));
		goods.setSellerName("x".repeat(100));
		goods.setManufacturer("x".repeat(100));
		goods.setOriginCountry("x".repeat(100));
		goods.setTaxType("x".repeat(20));
		goods.setShippingType("x".repeat(20));
		goods.setGoodsStatus("x".repeat(30));
		goods.setShortDescription("x".repeat(500));
		goods.setSearchKeywords("x".repeat(500));
		goods.setDetailDescription("가".repeat(21845));
		BigDecimal maximum = new BigDecimal("9999999999999.99");
		goods.setCostPrice(maximum);
		goods.setSalePrice(maximum);
		goods.setDiscountPrice(maximum);
		goods.setShippingFee(maximum);
		assertThat(controller.updateGoods("test-token", "test-service-key", 7L, goods).getStatusCode())
				.isEqualTo(HttpStatus.ACCEPTED);
		verify(updates).update(goods);
	}

	@Test
	void excessivePageNumbersReturn400ForHistoryAndGoodsList() throws Exception {
		ReflectionTestUtils.setField(controller, "originServiceKey", "test-service-key");
		when(persistence.selectGoodsInfo(7L)).thenReturn(validGoods());
		when(persistence.selectGoodsHistory(7L, Integer.MAX_VALUE, 100))
				.thenThrow(new IllegalArgumentException("page too large"));
		assertThat(controller.getGoodsHistory("test-service-key", 7L, Integer.MAX_VALUE, 100).getStatusCode())
				.isEqualTo(HttpStatus.BAD_REQUEST);
		when(persistence.selectGoodsList("GOODS_NAME", null, null, null, null, null, null, Integer.MAX_VALUE, 100))
				.thenThrow(new IllegalArgumentException("page too large"));
		assertThat(controller.getGoodsList("test-service-key", "GOODS_NAME", null, null, null, null, null, null,
				Integer.MAX_VALUE, 100).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		verifyNoInteractions(updates);
	}

	private GoodsInfoDto validGoods() {
		GoodsInfoDto goods = new GoodsInfoDto();
		goods.setGoodsInfoHistoryNo(10L);
		goods.setCategoryNo(1L);
		goods.setGoodsCode("test-goods");
		goods.setGoodsNameKo("테스트 상품");
		goods.setSellerName("테스트 판매자");
		goods.setGoodsStatus("READY");
		goods.setUseYn("Y");
		goods.setDisplayYn("Y");
		goods.setCostPrice(BigDecimal.ZERO);
		goods.setSalePrice(BigDecimal.TEN);
		goods.setStockQuantity(1);
		goods.setSafetyStockQuantity(0);
		goods.setTaxType("TAXABLE");
		goods.setShippingType("FREE");
		goods.setShippingFee(BigDecimal.ZERO);
		return goods;
	}
}
