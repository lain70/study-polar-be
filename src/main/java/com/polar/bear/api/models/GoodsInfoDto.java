package com.polar.bear.api.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Alias("goodsInfoDto")
public class GoodsInfoDto implements Serializable {
	private static final long serialVersionUID = -2024550559477689007L;
	private Long goodsNo;
	private String goodsCode;
	private String goodsNameKo;
	private String goodsNameEn;
	private Long brandNo;
	private String brandNameKo;
	private String brandNameEn;
	private Long categoryNo;
	private String categoryName;
	private String sellerName;
	private String manufacturer;
	private String originCountry;
	private BigDecimal costPrice;
	private BigDecimal salePrice;
	private BigDecimal discountPrice;
	private Integer stockQuantity;
	private Integer safetyStockQuantity;
	private String taxType;
	private String shippingType;
	private BigDecimal shippingFee;
	private String goodsStatus;
	private String useYn;
	private String displayYn;
	private LocalDateTime displayStartDate;
	private LocalDateTime displayEndDate;
	private String shortDescription;
	private String detailDescription;
	private String searchKeywords;
	private LocalDateTime regDate;
	private String regId;
	private LocalDateTime updtDate;
	private String updtId;
	private String representativeImageUrl;
	private List<GoodsImageDto> images;
}
