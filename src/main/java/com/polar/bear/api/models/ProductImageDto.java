package com.polar.bear.api.models;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductImageDto implements Serializable {
	private static final long serialVersionUID = 3428389769464672558L;
	private Long productImageNo;
	private Long productNo;
	private String imageUrl;
	private String originalFileName;
	private String representativeYn;
	private Integer sortOrder;
	private String regId;
}
