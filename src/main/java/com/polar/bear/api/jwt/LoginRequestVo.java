package com.polar.bear.api.jwt;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestVo implements Serializable{

	private static final long serialVersionUID = -3792229259992729296L;
	
	private String userId;
	private String userPwd;
	
	public LoginRequestVo() {}
	
	public LoginRequestVo(String userId, String userPwd) {
		this.userId = userId;
		this.userPwd = userPwd;
	}
}
