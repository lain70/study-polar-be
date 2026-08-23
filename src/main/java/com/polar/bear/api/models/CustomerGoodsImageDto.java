package com.polar.bear.api.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CustomerGoodsImageDto {
	private Long goodsImageNo;
	private String imageUrl;
	private String representativeYn;
	private Integer sortOrder;
}
