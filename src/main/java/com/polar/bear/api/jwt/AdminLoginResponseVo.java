package com.polar.bear.api.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminLoginResponseVo {
	private Integer adminNo;
	private String adminId;
	private String adminName;
	private String adminDepartment;
	private String adminPosition;
	private String accessToken;
	private String refreshToken;
}
