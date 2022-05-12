package com.polar.bear.api.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseVo {
	private Integer csrNo;
	private String csrId;
	private String accessToken;
	private String refreshToken;
	
}
