package com.polar.bear.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.polar.bear.api.config.GoodsRabbitConfig;
import com.polar.bear.api.models.GoodsInfoDto;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

class GoodsUpdateServiceTest {
	@Test
	void directUpdateWritesToDatabaseAndReturnsCompleted() throws Exception {
		GoodsInfoService persistence = mock(GoodsInfoService.class);
		GoodsInfoDto goods = new GoodsInfoDto();
		when(persistence.updateGoodsInfo(goods)).thenReturn(22L);
		assertThat(new DirectGoodsUpdateService(persistence).update(goods)).isFalse();
		assertThat(goods.getGoodsInfoHistoryNo()).isEqualTo(22L);
		verify(persistence).updateGoodsInfo(goods);
	}

	@Test
	void queuedUpdateWaitsForBrokerConfirmation() throws Exception {
		RabbitTemplate rabbit = confirmedRabbit(true, false);
		GoodsInfoDto goods = new GoodsInfoDto();
		assertThat(new QueuedGoodsUpdateService(rabbit).update(goods)).isTrue();
		verify(rabbit).convertAndSend(eq(""), eq(GoodsRabbitConfig.UPDATE_QUEUE), same(goods),
				any(MessagePostProcessor.class), any(CorrelationData.class));
	}

	@Test
	void nackAndUnroutableMessageAreNotReportedAsAccepted() {
		assertThatThrownBy(() -> new QueuedGoodsUpdateService(confirmedRabbit(false, false))
				.update(new GoodsInfoDto())).isInstanceOf(AmqpException.class);
		assertThatThrownBy(() -> new QueuedGoodsUpdateService(confirmedRabbit(true, true))
				.update(new GoodsInfoDto())).isInstanceOf(AmqpException.class);
	}

	@Test
	void connectionFailureIsPropagated() {
		RabbitTemplate rabbit = mock(RabbitTemplate.class);
		doThrow(new AmqpException("unavailable")).when(rabbit).convertAndSend(anyString(), anyString(),
				any(Object.class), any(MessagePostProcessor.class), any(CorrelationData.class));
		assertThatThrownBy(() -> new QueuedGoodsUpdateService(rabbit).update(new GoodsInfoDto()))
				.isInstanceOf(AmqpException.class);
	}

	@Test
	void consumerPropagatesDatabaseFailureForRetry() throws Exception {
		GoodsInfoService persistence = mock(GoodsInfoService.class);
		GoodsInfoDto goods = new GoodsInfoDto();
		doThrow(new IllegalStateException("database unavailable")).when(persistence).updateGoodsInfo(goods);
		assertThatThrownBy(() -> new GoodsUpdateConsumer(persistence).consume(goods))
				.isInstanceOf(IllegalStateException.class);
	}

	private RabbitTemplate confirmedRabbit(boolean ack, boolean returned) {
		RabbitTemplate rabbit = mock(RabbitTemplate.class);
		doAnswer(invocation -> {
			CorrelationData correlation = invocation.getArgument(4);
			if (returned) {
				correlation.setReturned(mock(ReturnedMessage.class));
			}
			correlation.getFuture().set(new CorrelationData.Confirm(ack, null));
			return null;
		}).when(rabbit).convertAndSend(anyString(), anyString(), any(Object.class),
				any(MessagePostProcessor.class), any(CorrelationData.class));
		return rabbit;
	}
}
