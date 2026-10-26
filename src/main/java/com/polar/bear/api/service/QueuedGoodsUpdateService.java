package com.polar.bear.api.service;

import java.util.concurrent.TimeUnit;

import com.polar.bear.api.config.GoodsRabbitConfig;
import com.polar.bear.api.models.GoodsInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("loc & !prd")
@RequiredArgsConstructor
public class QueuedGoodsUpdateService implements GoodsUpdateService {
	private final RabbitTemplate rabbitTemplate;

	@Override
	public boolean update(GoodsInfoDto goods) throws Exception {
		CorrelationData correlation = new CorrelationData();
		rabbitTemplate.convertAndSend("", GoodsRabbitConfig.UPDATE_QUEUE, goods, message -> {
			message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
			return message;
		}, correlation);
		CorrelationData.Confirm confirm;
		try {
			confirm = correlation.getFuture().get(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new AmqpException("상품 수정 접수 확인이 중단되었습니다.", e);
		}
		if (!confirm.isAck() || correlation.getReturned() != null) {
			throw new AmqpException("상품 수정 메시지를 큐에 저장하지 못했습니다.");
		}
		return true;
	}
}
