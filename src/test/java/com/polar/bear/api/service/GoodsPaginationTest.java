package com.polar.bear.api.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.polar.bear.api.mappers.GoodsInfoMapper;
import org.junit.jupiter.api.Test;

class GoodsPaginationTest {
	private final GoodsInfoMapper mapper = mock(GoodsInfoMapper.class);
	private final GoodsInfoService service = new GoodsInfoService(mapper, mock(GoodsHistoryService.class));

	@Test
	void overflowIsRejectedBeforeAnyQueryForBothLists() {
		assertThatThrownBy(() -> service.selectGoodsHistory(7L, Integer.MAX_VALUE, 100))
				.isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(() -> service.selectGoodsList("GOODS_NAME", null, null, null, null, null, null,
				Integer.MAX_VALUE, 100)).isInstanceOf(IllegalArgumentException.class);
		verifyNoInteractions(mapper);
	}

	@Test
	void lastValidPageIsCalculatedWithoutOverflow() throws Exception {
		int page = Integer.MAX_VALUE / 100 + 1;
		int offset = (int) (((long) page - 1) * 100);
		service.selectGoodsHistory(7L, page, 100);
		verify(mapper).selectGoodsInfoHistory(7L, offset, 100);
		verify(mapper).selectGoodsPriceHistory(7L, offset, 100);
		service.selectGoodsList("GOODS_NAME", null, null, null, null, null, null, page, 100);
		verify(mapper).selectGoodsList("GOODS_NAME", null, null, null, null, null, null, offset, 100);
	}

	@Test
	void firstInvalidPageIsRejectedBeforeAnyQuery() {
		int page = Integer.MAX_VALUE / 100 + 2;
		assertThatThrownBy(() -> service.selectGoodsHistory(7L, page, 100))
				.isInstanceOf(IllegalArgumentException.class);
		verifyNoInteractions(mapper);
	}

	@Test
	void existingPageAndSizeClampingStillWorks() throws Exception {
		java.util.Map<String, Object> result = service.selectGoodsHistory(7L, -1, 0);
		assertThat(result.get("page")).isEqualTo(1);
		assertThat(result.get("size")).isEqualTo(1);
		verify(mapper).selectGoodsInfoHistory(7L, 0, 1);
		service.selectGoodsHistory(7L, 2, Integer.MAX_VALUE);
		verify(mapper).selectGoodsInfoHistory(7L, 100, 100);
	}
}
