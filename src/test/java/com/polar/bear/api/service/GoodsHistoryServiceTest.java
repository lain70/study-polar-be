package com.polar.bear.api.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.polar.bear.api.mappers.GoodsInfoMapper;
import com.polar.bear.api.models.GoodsInfoDto;
import com.polar.bear.api.models.GoodsInfoHistoryDto;
import com.polar.bear.api.models.GoodsPriceHistoryDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.dao.DataIntegrityViolationException;

class GoodsHistoryServiceTest {
	private final GoodsInfoMapper mapper = mock(GoodsInfoMapper.class);
	private final ObjectMapper json = new ObjectMapper();
	private final GoodsHistoryService service = new GoodsHistoryService(mapper, json);

	@BeforeEach
	void generatedHistoryNumber() throws Exception {
		when(mapper.insertGoodsInfoHistory(any())).thenAnswer(invocation -> {
			GoodsInfoHistoryDto history = invocation.getArgument(0);
			history.setGoodsInfoHistoryNo(11L);
			return 1;
		});
	}

	@Test
	void informationChangesExcludePricesAndCompareDecimalValuesNumerically() throws Exception {
		GoodsInfoDto before = goods();
		GoodsInfoDto after = goods();
		after.setGoodsNameKo("new-name");
		after.setShippingFee(new BigDecimal("0.00"));
		assertThat(service.record(before, after, "UPDATE")).isEqualTo(11L);
		ArgumentCaptor<GoodsInfoHistoryDto> history = ArgumentCaptor.forClass(GoodsInfoHistoryDto.class);
		verify(mapper).insertGoodsInfoHistory(history.capture());
		assertThat(json.readTree(history.getValue().getChangedFields()).toString()).isEqualTo("[\"goodsNameKo\"]");
		assertThat(history.getValue().getAfterValues()).doesNotContain("salePrice", "costPrice", "discountPrice");
		verify(mapper, never()).insertGoodsPriceHistory(any());
	}

	@Test
	void priceChangeIsRecordedSeparatelyAndLinkedToInformationHistory() throws Exception {
		GoodsInfoDto before = goods();
		GoodsInfoDto after = goods();
		after.setSalePrice(new BigDecimal("30.00"));
		after.setDiscountPrice(BigDecimal.TEN);
		when(mapper.insertGoodsPriceHistory(any())).thenReturn(1);
		service.record(before, after, "UPDATE");
		ArgumentCaptor<GoodsPriceHistoryDto> price = ArgumentCaptor.forClass(GoodsPriceHistoryDto.class);
		verify(mapper).insertGoodsPriceHistory(price.capture());
		assertThat(price.getValue().getGoodsInfoHistoryNo()).isEqualTo(11L);
		assertThat(price.getValue().getBeforeSalePrice()).isEqualByComparingTo("20");
		assertThat(price.getValue().getAfterSalePrice()).isEqualByComparingTo("30");
		assertThat(price.getValue().getBeforeDiscountPrice()).isNull();
		assertThat(price.getValue().getAfterDiscountPrice()).isEqualByComparingTo("10");
	}

	@Test
	void registrationRecordsBothInitialHistories() throws Exception {
		when(mapper.insertGoodsPriceHistory(any())).thenReturn(1);
		service.record(null, goods(), "CREATE");
		ArgumentCaptor<GoodsInfoHistoryDto> history = ArgumentCaptor.forClass(GoodsInfoHistoryDto.class);
		verify(mapper).insertGoodsInfoHistory(history.capture());
		assertThat(history.getValue().getBeforeValues()).isNull();
		assertThat(history.getValue().getChangeType()).isEqualTo("CREATE");
		verify(mapper).insertGoodsPriceHistory(any());
	}

	@Test
	void priceHistoryFailureIsPropagatedToTransactionalCaller() throws Exception {
		GoodsInfoDto after = goods();
		after.setCostPrice(BigDecimal.ONE);
		when(mapper.insertGoodsPriceHistory(any())).thenThrow(new DataIntegrityViolationException("history unavailable"));
		assertThatThrownBy(() -> service.record(goods(), after, "UPDATE"))
				.isInstanceOf(DataIntegrityViolationException.class);
	}

	@Test
	void unchangedSaveAdvancesInformationHistoryWithoutDuplicatePriceHistory() throws Exception {
		service.record(goods(), goods(), "UPDATE");
		ArgumentCaptor<GoodsInfoHistoryDto> history = ArgumentCaptor.forClass(GoodsInfoHistoryDto.class);
		verify(mapper).insertGoodsInfoHistory(history.capture());
		assertThat(history.getValue().getChangedFields()).isEqualTo("[]");
		verify(mapper, never()).insertGoodsPriceHistory(any());
	}

	private GoodsInfoDto goods() {
		GoodsInfoDto goods = new GoodsInfoDto();
		goods.setGoodsNo(7L);
		goods.setGoodsNameKo("old-name");
		goods.setCostPrice(BigDecimal.TEN);
		goods.setSalePrice(new BigDecimal("20.00"));
		goods.setShippingFee(BigDecimal.ZERO);
		goods.setUpdtId("test-admin");
		return goods;
	}
}
