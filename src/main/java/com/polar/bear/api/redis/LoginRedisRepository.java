package com.polar.bear.api.redis;

import java.util.Map;

public interface LoginRedisRepository {
	
	void save(LoginRedisVo loginRedisVo);
	void update(LoginRedisVo loginRedisVo);
	void delete(String id);
	Map<String, LoginRedisVo> findAll();
	LoginRedisVo findById(String id);
	
}
