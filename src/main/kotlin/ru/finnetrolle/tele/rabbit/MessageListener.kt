package ru.finnetrolle.tele.rabbit

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.Header
import org.springframework.stereotype.Component
import org.telegram.telegrambots.api.methods.send.SendMessage
import ru.finnetrolle.tele.telegram.TelegramConnector

/**
 * Created by maxsyachin on 18.10.16.
 */

@Component
open class MessageListener {

    private val LOG = LoggerFactory.getLogger(MessageListener::class.java)

    @Autowired
    private lateinit var connector: TelegramConnector

    @RabbitListener(queues = arrayOf("\${rabbit.tosend.q}"))
    open fun processCallback(msg: ToSend) {
        LOG.debug("{PROCESS_MESSAGE} ${msg}")
        val message = SendMessage()
            .setReplyToMessageId(msg.replyToMessageId)
            .setChatId(msg.chatId)
            .setText(msg.text)
        connector.send(message)
    }

}