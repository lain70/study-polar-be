package com.polar.bear.api.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.polar.bear.api.mappers.CartMapper;
import com.polar.bear.api.models.GoodsInfoDto;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

class CartServiceTest {
	private final CartMapper mapper = mock(CartMapper.class);
	private final CartService service = new CartService(mapper);

	private void available(int stock) {
		GoodsInfoDto goods = new GoodsInfoDto();
		goods.setStockQuantity(stock);
		when(mapper.selectAvailableGoodsForUpdate(7L)).thenReturn(goods);
	}

	@Test
	void duplicateAdditionAccumulatesAndKeepsTheTrustedOwner() {
		available(10);
		when(mapper.selectQuantity(1, 7L)).thenReturn(2);
		service.addItem(1, 7L, 3);
		verify(mapper).updateQuantity(1, 7L, 5);
		verify(mapper, never()).insertItem(anyInt(), anyLong(), anyInt());
	}

	@Test
	void invalidAndOverflowingQuantitiesCannotBeSaved() {
		for (Integer quantity : new Integer[] {null, 0, -1}) {
			assertThatThrownBy(() -> service.addItem(1, 7L, quantity)).isInstanceOf(ResponseStatusException.class);
		}
		available(Integer.MAX_VALUE);
		when(mapper.selectQuantity(1, 7L)).thenReturn(Integer.MAX_VALUE);
		assertThatThrownBy(() -> service.addItem(1, 7L, 1)).isInstanceOf(ResponseStatusException.class);
		verify(mapper, never()).updateQuantity(anyInt(), anyLong(), anyInt());
	}

	@Test
	void unavailableAndOverStockItemsAreRejected() {
		assertThatThrownBy(() -> service.addItem(1, 7L, 1)).isInstanceOf(ResponseStatusException.class);
		available(2);
		assertThatThrownBy(() -> service.addItem(1, 7L, 3)).isInstanceOf(ResponseStatusException.class);
		verify(mapper, never()).insertItem(anyInt(), anyLong(), anyInt());
	}

	@Test
	void anotherMembersItemCannotBeUpdated() {
		available(10);
		when(mapper.selectQuantity(1, 7L)).thenReturn(null);
		assertThatThrownBy(() -> service.updateQuantity(1, 7L, 2)).isInstanceOf(ResponseStatusException.class);
		verify(mapper, never()).updateQuantity(anyInt(), anyLong(), anyInt());
	}

	@Test
	void deletionAndListingAreScopedToTheMember() {
		service.getItems(3);
		service.deleteItem(3, 7L);
		verify(mapper).selectItems(3);
		verify(mapper).deleteItem(3, 7L);
	}
}
