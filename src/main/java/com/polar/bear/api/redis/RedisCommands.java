package com.polar.bear.api.redis;

import java.util.List;

import org.springframework.stereotype.Component;
import io.lettuce.core.dynamic.Commands;
import io.lettuce.core.dynamic.annotation.Command;

@Component
public interface RedisCommands<T> extends Commands {
	
	@Command("GET")
    String get(String key);
	
	@Command("INCR")
	void incr(String key);
	
	@Command("EXPIRE")
	void expire(String key, long seconds);
	
	@Command("HINCRBY")
	void hincrby(String key, String field, long increment);
	
	@Command("ZADD")
	void zadd(String key, String score, String member);
	
	@Command("ZINCRBY")
	void zincrby(String key, long amount, String member);
	
	@Command("ZRANGE")
    List<String> zrange(String key, long start, long stop);
	
	@Command("ZREVRANGE")
    List<String> zrevrange(String key, long start, long stop);
}
