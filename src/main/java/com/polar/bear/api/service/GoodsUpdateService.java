package com.polar.bear.api.service;

import com.polar.bear.api.models.GoodsInfoDto;

public interface GoodsUpdateService {
	boolean update(GoodsInfoDto goods) throws Exception;
}
