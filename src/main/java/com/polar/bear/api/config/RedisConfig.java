package com.polar.bear.api.config;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import com.polar.bear.api.redis.RedisCommands;
import com.polar.bear.api.redis.RedisProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.dynamic.RedisCommandFactory;

@Configuration
@EnableRedisRepositories(basePackages = "com.polar.bear.api.redis")
@EnableConfigurationProperties(RedisProperty.class)
public class RedisConfig {
	
	@Autowired
	private RedisProperty redis;
	
	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
		redisStandaloneConfiguration.setHostName(redis.getHost());
		redisStandaloneConfiguration.setPort(redis.getPort());
		redisStandaloneConfiguration.setPassword(redis.getPassword());
		
		LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisStandaloneConfiguration);		
		return lettuceConnectionFactory;
	}
	
	@Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return redisTemplate;
    }
	
	@Bean
	public RedisCommands<?> redisCommands(){
		RedisURI redisURI = RedisURI.Builder.redis(redis.getHost(), redis.getPort())
				.withPassword(redis.getPassword()).build();
		
		RedisClient client = RedisClient.create(redisURI);
		RedisCommandFactory redisCommandFactory = new RedisCommandFactory(client.connect(), Arrays.asList(new StringCodec(StandardCharsets.UTF_8)));
		return redisCommandFactory.getCommands(RedisCommands.class);
	}

}
