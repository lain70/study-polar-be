package com.polar.bear.api.service;

import java.util.List;
import com.polar.bear.api.mappers.CartMapper;
import com.polar.bear.api.models.CartItemDto;
import com.polar.bear.api.models.GoodsInfoDto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {
	private final CartMapper cartMapper;

	public List<CartItemDto> getItems(Integer userNo) {
		return cartMapper.selectItems(userNo);
	}

	@Transactional
	public void addItem(Integer userNo, Long goodsNo, Integer quantity) {
		validateQuantity(quantity);
		GoodsInfoDto goods = requireGoods(goodsNo);
		Integer current = cartMapper.selectQuantity(userNo, goodsNo);
		// Lock the goods row first so concurrent additions cannot lose quantities or exceed stock.
		long total = (long) (current == null ? 0 : current) + quantity;
		validateStock(goods, total);
		if (current == null) {
			cartMapper.insertItem(userNo, goodsNo, (int) total);
		} else {
			cartMapper.updateQuantity(userNo, goodsNo, (int) total);
		}
	}

	@Transactional
	public void updateQuantity(Integer userNo, Long goodsNo, Integer quantity) {
		validateQuantity(quantity);
		GoodsInfoDto goods = requireGoods(goodsNo);
		if (cartMapper.selectQuantity(userNo, goodsNo) == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "장바구니 상품을 찾을 수 없습니다.");
		}
		validateStock(goods, quantity);
		cartMapper.updateQuantity(userNo, goodsNo, quantity);
	}

	public void deleteItem(Integer userNo, Long goodsNo) {
		cartMapper.deleteItem(userNo, goodsNo);
	}

	private GoodsInfoDto requireGoods(Long goodsNo) {
		if (goodsNo == null || goodsNo <= 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "상품 번호가 올바르지 않습니다.");
		}
		GoodsInfoDto goods = cartMapper.selectAvailableGoodsForUpdate(goodsNo);
		if (goods == null) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "현재 담을 수 없는 상품입니다.");
		}
		return goods;
	}

	private void validateQuantity(Integer quantity) {
		if (quantity == null || quantity < 1) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수량은 1 이상이어야 합니다.");
		}
	}

	private void validateStock(GoodsInfoDto goods, long quantity) {
		if (goods.getStockQuantity() == null || quantity > goods.getStockQuantity()) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "담은 수량이 현재 재고를 초과합니다.");
		}
	}
}
