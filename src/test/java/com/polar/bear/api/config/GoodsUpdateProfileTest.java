package com.polar.bear.api.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.polar.bear.api.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;

import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class GoodsUpdateProfileTest {
	private final ApplicationContextRunner runner = new ApplicationContextRunner()
			.withInitializer(new ConfigDataApplicationContextInitializer())
			.withUserConfiguration(AutoConfig.class)
			.withBean(ObjectMapper.class, ObjectMapper::new)
			.withBean(GoodsInfoService.class, () -> mock(GoodsInfoService.class))
			.withUserConfiguration(GoodsRabbitConfig.class, QueuedGoodsUpdateService.class,
					DirectGoodsUpdateService.class, GoodsUpdateConsumer.class);

	@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class,
			RedisAutoConfiguration.class, RedisRepositoriesAutoConfiguration.class})
	static class AutoConfig {
	}

	@Test
	void localUsesQueue() {
		runner.withPropertyValues("spring.profiles.active=loc", "RABBITMQ_HOST=localhost",
				"RABBITMQ_USERNAME=test", "RABBITMQ_PASSWORD=test").run(context -> {
			assertThat(context).hasSingleBean(QueuedGoodsUpdateService.class);
			assertThat(context).hasSingleBean(RabbitTemplate.class);
			RabbitTemplate template = context.getBean(RabbitTemplate.class);
			assertThat(template).isSameAs(context.getBean("rabbitTemplate"));
			assertThat(template.getMessageConverter()).isInstanceOf(Jackson2JsonMessageConverter.class);
			assertThat(template.isMandatoryFor(new Message(new byte[0], new MessageProperties()))).isTrue();
			CachingConnectionFactory connection = context.getBean(CachingConnectionFactory.class);
			assertThat(connection.isPublisherConfirms()).isTrue();
			assertThat(connection.isPublisherReturns()).isTrue();
			assertThat(context).hasSingleBean(GoodsUpdateConsumer.class);
			assertThat(context).doesNotHaveBean(DirectGoodsUpdateService.class);
		});
	}

	@Test
	void productionHasNoRabbitConnectionEvenWhenLocalAlsoActive() {
		for (String profiles : new String[] {"prd", "loc,prd"}) {
			runner.withPropertyValues("spring.profiles.active=" + profiles).run(context -> {
				assertThat(context).hasSingleBean(DirectGoodsUpdateService.class);
				assertThat(context).doesNotHaveBean(QueuedGoodsUpdateService.class);
				assertThat(context).doesNotHaveBean(GoodsUpdateConsumer.class);
				assertThat(context).doesNotHaveBean(ConnectionFactory.class);
				assertThat(context).doesNotHaveBean(RabbitTemplate.class);
			});
		}
	}
}
