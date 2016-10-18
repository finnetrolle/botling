package ru.finnetrolle.tele.config

import org.slf4j.LoggerFactory
import org.springframework.amqp.core.AmqpAdmin
import org.springframework.amqp.rabbit.annotation.EnableRabbit
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.retry.backoff.ExponentialBackOffPolicy
import org.springframework.retry.support.RetryTemplate

@Configuration
@EnableRabbit
open class RabbitConfig {

    private val log = LoggerFactory.getLogger(RabbitConfig::class.java)

    @Value("\${rabbit.connection.hostname}")
    private val hostname: String? = null

    @Value("\${rabbit.connection.username}")
    private val username: String? = null

    @Value("\${rabbit.connection.password}")
    private val password: String? = null

    @Bean
    open fun connectionFactory(): ConnectionFactory {
        log.info("Creating connection factory")
        val factory = CachingConnectionFactory()
        factory.setAddresses(hostname)
        factory.username = username
        factory.setPassword(password)
        factory.setConnectionTimeout(1000)
        factory.setRequestedHeartBeat(100)
        return factory
    }

    @Bean
    open fun amqpAdmin(): AmqpAdmin {
        log.info("Creating amqp admin")
        val admin = RabbitAdmin(connectionFactory())
        admin.isAutoStartup = true
        return admin
    }

    @Bean
    open fun rabbitTemplate(): RabbitTemplate {
        log.info("Creating rabbit template")
        val template = RabbitTemplate(connectionFactory())
        template.messageConverter = jsonMessageConverter()
        val retry = RetryTemplate()
        val backOffPolicy = ExponentialBackOffPolicy()
        backOffPolicy.initialInterval = 500
        backOffPolicy.multiplier = 2.0
        backOffPolicy.maxInterval = 3600000
        retry.setBackOffPolicy(backOffPolicy)
        template.setRetryTemplate(retry)
        return template
    }

    @Bean
    open fun jsonMessageConverter(): MessageConverter {
        return Jackson2JsonMessageConverter()
    }

    @Bean
    open fun rabbitListenerContainerFactory(): SimpleRabbitListenerContainerFactory {
        log.info("Creating container factory")
        val factory = SimpleRabbitListenerContainerFactory()
        factory.setConnectionFactory(connectionFactory())
        factory.setMessageConverter(jsonMessageConverter())
        factory.setConcurrentConsumers(2)
        factory.setPrefetchCount(1)
        factory.setReceiveTimeout(10000L)
        factory.setMaxConcurrentConsumers(2)
        return factory
    }

}