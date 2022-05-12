package com.polar.bear.api.redis;

import java.io.Serializable;
import java.util.Date;

import com.polar.bear.api.models.CsrInfoDto;
import org.springframework.data.redis.core.RedisHash;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@RedisHash("token")
public class LoginRedisVo implements Serializable{
	
	private static final long serialVersionUID = 6294912149709163209L;
	
	private String csrKey;
	private String csrId;
	private String type;
	private CsrInfoDto csrInfoDto;
	private String accessToken;
	private String refreshToken;
	private long lastUpdateTime;
	
	@Builder
	public LoginRedisVo(String csrKey, String csrId, String type, CsrInfoDto csrInfoDto
			, String accessToken, String refreshToken, long lastUpdateTime) {
		this.csrKey = csrKey;
		this.csrId = csrId;
		this.type = type;
		this.csrInfoDto = csrInfoDto;
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.lastUpdateTime = lastUpdateTime;
	}
	
	public void refreshInfo(CsrInfoDto csrInfoDto) {
		this.csrInfoDto = csrInfoDto;
		this.lastUpdateTime = new Date(System.currentTimeMillis()).getTime();
	}
	
	
}
