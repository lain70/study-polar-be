package com.polar.bear.api.service;

import com.polar.bear.api.config.GoodsRabbitConfig;
import com.polar.bear.api.models.GoodsInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("loc & !prd")
@RequiredArgsConstructor
public class GoodsUpdateConsumer {
	private final GoodsInfoService goodsInfoService;

	@RabbitListener(queues = GoodsRabbitConfig.UPDATE_QUEUE)
	public void consume(GoodsInfoDto goods) throws Exception {
		goodsInfoService.updateGoodsInfo(goods);
	}
}
