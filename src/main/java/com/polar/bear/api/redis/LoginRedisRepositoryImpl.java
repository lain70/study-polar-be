package com.polar.bear.api.redis;

import java.util.Map;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@SuppressWarnings("ALL")
@Repository
public class LoginRedisRepositoryImpl implements LoginRedisRepository {
	
	private RedisTemplate<String, Object> redisTemplate;
	
	private HashOperations hashOperations;
	
	
	public LoginRedisRepositoryImpl(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
        this.hashOperations = redisTemplate.opsForHash();
	}

	@Override
	public void save(LoginRedisVo loginRedisVo) {
		this.hashOperations.put("polar_token", loginRedisVo.getCsrKey(), loginRedisVo);

	}

	@Override
	public void update(LoginRedisVo loginRedisVo) {
		this.hashOperations.put("polar_token", loginRedisVo.getCsrKey(), loginRedisVo);

	}

	@Override
	public void delete(String id) {
		this.hashOperations.delete("polar_token", id);
	}

	@Override
	public Map<String, LoginRedisVo> findAll() {
		return this.hashOperations.entries("polar_token");
	}

	@Override
	public LoginRedisVo findById(String id) {
		return (LoginRedisVo)this.hashOperations.get("polar_token", id);
	}

}
