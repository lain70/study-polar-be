package com.polar.bear.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateConfigurer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("loc & !prd")
public class GoodsRabbitConfig {
	public static final String UPDATE_QUEUE = "goods.update";
	public static final String FAILED_QUEUE = "goods.update.failed";

	@Bean
	public RabbitTemplate rabbitTemplate(RabbitTemplateConfigurer configurer, ConnectionFactory connectionFactory) {
		RabbitTemplate template = new RabbitTemplate();
		configurer.configure(template, connectionFactory);
		return template;
	}

	@Bean
	public Declarables goodsUpdateQueues() {
		return new Declarables(
				QueueBuilder.durable(UPDATE_QUEUE)
						.withArgument("x-single-active-consumer", true)
						.deadLetterExchange("").deadLetterRoutingKey(FAILED_QUEUE).build(),
				QueueBuilder.durable(FAILED_QUEUE).build());
	}

	@Bean
	public Jackson2JsonMessageConverter goodsMessageConverter(ObjectMapper objectMapper) {
		return new Jackson2JsonMessageConverter(objectMapper);
	}
}
