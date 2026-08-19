package com.polar.bear.api.models;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Alias("brandInfoDto")
public class BrandInfoDto implements Serializable {
	private static final long serialVersionUID = 3077374347062107925L;
	private Long brandNo;
	private String brandNameKo;
	private String brandNameEn;
	private String brandCountry;
	private String brandLogoImageUrl;
	private String displayYn;
	private String useYn;
	private LocalDateTime updtDate;
	private String updtId;
	private LocalDateTime regDate;
	private String regId;
}
