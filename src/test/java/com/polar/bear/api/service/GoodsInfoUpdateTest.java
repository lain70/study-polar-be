package com.polar.bear.api.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.polar.bear.api.mappers.GoodsInfoMapper;
import com.polar.bear.api.models.GoodsInfoDto;
import org.junit.jupiter.api.Test;
import org.springframework.dao.OptimisticLockingFailureException;

class GoodsInfoUpdateTest {
	private final GoodsInfoMapper mapper = mock(GoodsInfoMapper.class);
	private final GoodsHistoryService history = mock(GoodsHistoryService.class);
	private final GoodsInfoService service = new GoodsInfoService(mapper, history);

	@Test
	void missingHistoryNumberNeverReachesDatabase() {
		assertThatThrownBy(() -> service.updateGoodsInfo(new GoodsInfoDto()))
				.isInstanceOf(IllegalArgumentException.class);
		verifyNoInteractions(mapper, history);
	}

	@Test
	void conflictIsRejectedWhileHoldingGoodsLock() throws Exception {
		when(mapper.selectGoodsInfoForUpdate(7L)).thenReturn(goods());
		when(mapper.selectLatestGoodsInfoHistoryNoForUpdate(7L)).thenReturn(11L);
		assertThatThrownBy(() -> service.updateGoodsInfo(goods()))
				.isInstanceOf(OptimisticLockingFailureException.class);
		verify(mapper, never()).updateGoodsInfo(any());
		verifyNoInteractions(history);
	}

	@Test
	void deletedGoodsIsDistinguishedFromHistoryConflict() throws Exception {
		assertThatThrownBy(() -> service.updateGoodsInfo(goods()))
				.isInstanceOf(IllegalArgumentException.class).hasMessage("상품을 찾을 수 없습니다.");
		verifyNoInteractions(history);
	}

	@Test
	void successfulUpdateRecordsHistoryWithoutMutatingMessageToken() throws Exception {
		GoodsInfoDto goods = goods();
		GoodsInfoDto before = goods();
		GoodsInfoDto after = goods();
		when(mapper.selectGoodsInfoForUpdate(7L)).thenReturn(before, after);
		when(mapper.selectLatestGoodsInfoHistoryNoForUpdate(7L)).thenReturn(10L);
		when(mapper.updateGoodsInfo(goods)).thenReturn(1);
		when(history.record(before, after, "UPDATE")).thenReturn(11L);
		assertThat(service.updateGoodsInfo(goods)).isEqualTo(11L);
		assertThat(goods.getGoodsInfoHistoryNo()).isEqualTo(10L);
		org.mockito.InOrder order = inOrder(mapper, history);
		order.verify(mapper).selectGoodsInfoForUpdate(7L);
		order.verify(mapper).selectLatestGoodsInfoHistoryNoForUpdate(7L);
		order.verify(mapper).updateGoodsInfo(goods);
		order.verify(mapper).selectGoodsInfoForUpdate(7L);
		order.verify(history).record(before, after, "UPDATE");
	}

	private GoodsInfoDto goods() {
		GoodsInfoDto goods = new GoodsInfoDto();
		goods.setGoodsNo(7L);
		goods.setGoodsInfoHistoryNo(10L);
		return goods;
	}
}
