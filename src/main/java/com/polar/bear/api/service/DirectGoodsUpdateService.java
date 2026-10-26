package com.polar.bear.api.service;

import com.polar.bear.api.models.GoodsInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("!loc | prd")
@RequiredArgsConstructor
public class DirectGoodsUpdateService implements GoodsUpdateService {
	private final GoodsInfoService goodsInfoService;

	@Override
	public boolean update(GoodsInfoDto goods) throws Exception {
		Long historyNo = goodsInfoService.updateGoodsInfo(goods);
		goods.setGoodsInfoHistoryNo(historyNo);
		return false;
	}
}
