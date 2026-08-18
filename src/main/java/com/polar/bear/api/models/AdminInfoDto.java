package com.polar.bear.api.models;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(exclude = "adminPwd")
@NoArgsConstructor
@Alias("adminInfoDto")
public class AdminInfoDto implements Serializable {

	private static final long serialVersionUID = -2880108961145231486L;

	private Integer adminNo;
	private String adminId;
	private String adminPwd;
	private String adminName;
	private String adminPhone;
	private String adminDepartment;
	private String adminPosition;
	private String adminStatus;
	private String useYn;
	private LocalDateTime regDate;
	private String regId;
	private LocalDateTime updtDate;
	private String updtId;
}
