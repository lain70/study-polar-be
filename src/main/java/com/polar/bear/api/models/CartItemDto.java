package com.polar.bear.api.models;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemDto {
	private Long goodsNo;
	private String goodsNameKo;
	private String representativeImageUrl;
	private Integer quantity;
	private Integer stockQuantity;
	private BigDecimal unitPrice;
	private boolean available;
}
