package com.polar.bear.api.models;

import java.math.BigDecimal;

import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Alias("customerProductDto")
public class CustomerProductDto {
	private Long productNo;
	private String productNameKo;
	private String productNameEn;
	private String brandName;
	private String categoryName;
	private BigDecimal salePrice;
	private BigDecimal discountPrice;
	private String productStatus;
	private String representativeImageUrl;
}
