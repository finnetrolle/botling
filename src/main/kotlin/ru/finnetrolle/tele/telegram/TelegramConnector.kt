package ru.finnetrolle.tele.telegram

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import ru.finnetrolle.tele.rabbit.MessageProvider
import ru.finnetrolle.tele.telegram.api.BotApi
import ru.finnetrolle.telebot.service.telegram.api.BotApiExtender
import javax.annotation.PostConstruct

/**
 * Created by maxsyachin on 18.10.16.
 */

@Component
open class TelegramConnector {

    private val LOG = LoggerFactory.getLogger(TelegramConnector::class.java)

    @Autowired
    private lateinit var messageProvider: MessageProvider

    @Value("\${bot.token}")
    private lateinit var botToken: String

    @Value("\${bot.username}")
    private lateinit var botUsername: String

    private var api: BotApi? = null

    @PostConstruct
    open fun init() {
        LOG.info("Telegram bot is enabled")
        try {
            api = BotApiExtender(botUsername, botToken, { msg -> messageProvider.processMessage(msg) })
        } catch (e: Exception) {
            LOG.error("Bot cannot be started", e)
        }
    }

    open fun send(message: SendMessage) {
        if (api != null) {
            api!!.send(message)
        }
    }

}