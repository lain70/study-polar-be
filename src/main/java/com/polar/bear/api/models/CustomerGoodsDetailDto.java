package com.polar.bear.api.models;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Alias("customerGoodsDetailDto")
public class CustomerGoodsDetailDto {
	private Long goodsNo;
	private String goodsNameKo;
	private String goodsNameEn;
	private String brandNameKo;
	private String brandNameEn;
	private String categoryName;
	private String sellerName;
	private String manufacturer;
	private String originCountry;
	private BigDecimal salePrice;
	private BigDecimal discountPrice;
	private String taxType;
	private String shippingType;
	private BigDecimal shippingFee;
	private String goodsStatus;
	private String shortDescription;
	private String detailDescription;
	private List<CustomerGoodsImageDto> images;
}
