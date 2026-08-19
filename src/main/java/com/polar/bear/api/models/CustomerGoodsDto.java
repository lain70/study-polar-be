package com.polar.bear.api.models;

import java.math.BigDecimal;

import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Alias("customerGoodsDto")
public class CustomerGoodsDto {
	private Long goodsNo;
	private String goodsNameKo;
	private String goodsNameEn;
	private Long brandNo;
	private String brandNameKo;
	private String brandNameEn;
	private Long categoryNo;
	private String categoryName;
	private BigDecimal salePrice;
	private BigDecimal discountPrice;
	private String goodsStatus;
	private String representativeImageUrl;
}
