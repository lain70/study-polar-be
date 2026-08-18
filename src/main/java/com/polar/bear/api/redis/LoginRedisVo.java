package com.polar.bear.api.redis;

import java.io.Serializable;
import java.util.Date;

import com.polar.bear.api.models.CsrInfoDto;
import com.polar.bear.api.models.AdminInfoDto;
import com.polar.bear.api.models.UserInfoDto;
import org.springframework.data.redis.core.RedisHash;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@RedisHash("token")
public class LoginRedisVo implements Serializable{
	
	private static final long serialVersionUID = 6294912149709163209L;
	
	private String userKey;
	private String userId;
	private String type;
	private UserInfoDto userInfoDto;
	private CsrInfoDto csrInfoDto;
	private AdminInfoDto adminInfoDto;
	private String accessToken;
	private String refreshToken;
	private long lastUpdateTime;
	
	@Builder
	public LoginRedisVo(String userKey, String userId, String type, UserInfoDto userInfoDto
			, String accessToken, String refreshToken, long lastUpdateTime) {
		this.userKey = userKey;
		this.userId = userId;
		this.type = type;
		this.userInfoDto = userInfoDto;
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.lastUpdateTime = lastUpdateTime;
	}

	public LoginRedisVo(String userKey, String userId, String type, AdminInfoDto adminInfoDto
			, String accessToken, String refreshToken, long lastUpdateTime) {
		this.userKey = userKey;
		this.userId = userId;
		this.type = type;
		this.adminInfoDto = adminInfoDto;
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.lastUpdateTime = lastUpdateTime;
	}
	
	public void refreshInfo(UserInfoDto userInfoDto) {
		this.userInfoDto = userInfoDto;
		this.lastUpdateTime = new Date(System.currentTimeMillis()).getTime();
	}
	
	
}
