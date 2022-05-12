package com.polar.bear.api.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "spring.redis")
public class RedisProperty {
	private String host;
	private int port;
	private String password;
}
