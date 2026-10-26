package com.polar.bear.api.models;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoodsInfoHistoryDto {
	private Long goodsInfoHistoryNo;
	private Long goodsNo;
	private String changeType;
	@JsonRawValue
	private String beforeValues;
	@JsonRawValue
	private String afterValues;
	@JsonRawValue
	private String changedFields;
	private LocalDateTime regDate;
	private String regId;
}
