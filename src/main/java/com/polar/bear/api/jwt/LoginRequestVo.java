package com.polar.bear.api.jwt;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestVo implements Serializable{

	private static final long serialVersionUID = -3792229259992729296L;
	
	private String csrId;
	private String csrPwd;
	private String type;
	
	public LoginRequestVo() {}
	
	public LoginRequestVo(String csrId, String csrPwd, String type) {
		this.csrId = csrId;
		this.csrPwd = csrPwd;
		this.type = type;
	}
}
