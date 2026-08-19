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
@Alias("categoryInfoDto")
public class CategoryInfoDto implements Serializable {
	private static final long serialVersionUID = 8170851640468261043L;
	private Long categoryNo;
	private String categoryName;
	private Integer categoryDepth;
	private Long parentCategoryNo;
	private String useYn;
	private String displayYn;
	private LocalDateTime updtDate;
	private String updtId;
	private LocalDateTime regDate;
	private String regId;
}
