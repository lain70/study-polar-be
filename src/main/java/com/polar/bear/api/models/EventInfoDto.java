package com.polar.bear.api.models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Alias("eventInfoDto")
public class EventInfoDto implements Serializable {
	private static final long serialVersionUID = 6504527039803439715L;
	private Long eventNo;
	private String eventTitle;
	private String useYn;
	private String displayYn;
	private LocalDateTime eventStartDate;
	private LocalDateTime eventEndDate;
	private LocalDateTime regDate;
	private String regId;
	private LocalDateTime updtDate;
	private String updtId;
	private String mainImageUrl;
	private String progressStatus;
	private List<Long> goodsNos;
}
