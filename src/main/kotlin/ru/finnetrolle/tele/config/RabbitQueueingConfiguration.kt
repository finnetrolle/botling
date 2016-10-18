
package ru.finnetrolle.tele.config

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.Declarable
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.annotation.EnableRabbit
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*

/**
 * Created by maxsyachin on 17.10.16.
 */

@Configuration
@EnableRabbit
open class RabbitQueueingConfiguration {

    private val X_DEAD_LETTER_PARAM = "x-dead-letter-exchange"
    private val PREFIX_DEAD = "dead_"
    private val POSTFIX_EXCHANGE = "_ex"
    private val X_TTL = "x-message-ttl"

    @Value("\${rabbit.received.q}")
    private lateinit var receivedQueue: String

    @Value("\${rabbit.received.e}")
    private lateinit var receivedExchange: String

    @Value("\${rabbit.tosend.q}")
    private lateinit var toSendQueue: String

    @Value("\${rabbit.tosend.e}")
    private lateinit var toSendExchange: String

    @Value("\${rabbit.throttle.time}")
    private val throttleTime: Long? = null

    @Bean
    open fun queuesConfiguration() = listOf<Declarable>()
                .plus(addThrottledCycledQueue(receivedQueue, throttleTime))
                .plus(addThrottledCycledQueue(toSendQueue, throttleTime))
                .plus(DirectExchange(receivedExchange, true, false))
                .plus(Binding(receivedQueue, Binding.DestinationType.QUEUE, receivedExchange, "", null))
                .plus(DirectExchange(toSendExchange, true, false))
                .plus(Binding(toSendQueue, Binding.DestinationType.QUEUE, toSendExchange, "", null))

    private fun addThrottledCycledQueue(queueName: String, throttleTime: Long?): List<Declarable> {
        val throttle = throttleTime ?: 0L
        val deadQueueName = PREFIX_DEAD + queueName
        val deadExName = PREFIX_DEAD + queueName + POSTFIX_EXCHANGE
        val exName = queueName + POSTFIX_EXCHANGE
        val deadQParams = HashMap<String, Any>()
        deadQParams.put(X_DEAD_LETTER_PARAM, exName)
        deadQParams.put(X_TTL, throttle)
        return Arrays.asList<Declarable>(
                Queue(queueName, true, false, false, Collections.singletonMap<String, Any>(X_DEAD_LETTER_PARAM, deadExName)),
                Queue(deadQueueName, true, false, false, deadQParams),
                DirectExchange(deadExName, true, false),
                DirectExchange(exName, true, false),
                Binding(queueName, Binding.DestinationType.QUEUE, exName, "", null),
                Binding(deadQueueName, Binding.DestinationType.QUEUE, deadExName, "", null))
    }

}