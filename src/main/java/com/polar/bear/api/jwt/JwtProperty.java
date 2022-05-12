package com.polar.bear.api.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "spring.jwt")
public class JwtProperty {
	private String secret;
	private Integer strength;
	private Delay delay = new Delay();
	
	@Data
	@ConfigurationProperties(prefix = "spring.jwt.delay")
	public class Delay {
		
		private long refresh;
		private long access;
	}
}
