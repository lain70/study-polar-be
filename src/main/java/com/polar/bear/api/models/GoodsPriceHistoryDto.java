package com.polar.bear.api.models;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoodsPriceHistoryDto {
	private Long goodsPriceHistoryNo;
	private Long goodsInfoHistoryNo;
	private Long goodsNo;
	private String changeType;
	private BigDecimal beforeCostPrice;
	private BigDecimal afterCostPrice;
	private BigDecimal beforeSalePrice;
	private BigDecimal afterSalePrice;
	private BigDecimal beforeDiscountPrice;
	private BigDecimal afterDiscountPrice;
	private LocalDateTime regDate;
	private String regId;
}
