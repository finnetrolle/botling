package ru.finnetrolle.tele.rabbit

import org.slf4j.LoggerFactory
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.telegram.telegrambots.api.objects.Message

/**
 * Created by maxsyachin on 18.10.16.
 */

@Component
open class MessageProvider {

    private val LOG = LoggerFactory.getLogger(MessageProvider::class.java)

    @Autowired
    private lateinit var template: AmqpTemplate

    @Value("\${rabbit.received.e}")
    private lateinit var exchangeName: String

    open fun processMessage(message: Message) {
        LOG.debug("PUSHING ${message.text}")
        template.convertAndSend(exchangeName, "", message)
    }

}