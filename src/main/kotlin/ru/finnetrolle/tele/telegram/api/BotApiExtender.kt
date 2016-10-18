package ru.finnetrolle.telebot.service.telegram.api

import org.slf4j.LoggerFactory
import org.telegram.telegrambots.TelegramBotsApi
import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.objects.Message
import org.telegram.telegrambots.api.objects.Update
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.exceptions.TelegramApiException
import org.telegram.telegrambots.exceptions.TelegramApiRequestException
import ru.finnetrolle.tele.telegram.api.BotApi

/**
 * Telegram bot
 * Licence: MIT
 * Author: Finne Trolle
 */

open class BotApiExtender(
        val name: String,
        val token: String,
        val processIncomingMessage: (Message) -> Unit
) : BotApi {

    private val LOG = LoggerFactory.getLogger(BotApiExtender::class.java)
    private val BLOCKED_BOT_MESSAGE: String = "Bot was blocked by the user"

    private var api = object : TelegramLongPollingBot() {
        override fun getBotUsername() = name

        override fun getBotToken() = token

        override fun onUpdateReceived(request: Update) {
            when {
                request.hasMessage() -> {
                    LOG.trace("Received request from chatId = ${request.message?.chatId} with text = ${request.message?.text}")
                    processIncomingMessage.invoke(request.message)
                }
                request.hasChosenInlineQuery() -> {
                    LOG.warn("Not supported format - ChosenInlineQuery from ${request.chosenInlineQuery.from.id}")
                }
                request.hasInlineQuery() -> {
                    LOG.warn("Not supported format - InlineQuery from ${request.inlineQuery.from.id}")
                }
                else -> {
                    LOG.warn("Impossible Exception for unknown request type")
                }
            }
        }
    }

    init {
        try {
            TelegramBotsApi().registerBot(api)
        } catch (e: TelegramApiRequestException) {
            LOG.error("Can't connect to telegram", e)
            LOG.error("Response is ${e.apiResponse}")
            LOG.error("Exit")
            System.exit(1)
        }
    }



    override fun send(message: SendMessage): BotApi.Send {
        try {
            val start = System.currentTimeMillis()
            LOG.trace("Trying to send response to ${message.chatId}")
            return BotApi.Send.Success(
            api.sendMessage(message).chatId,
            System.currentTimeMillis() - start)
        } catch (e: TelegramApiRequestException) {
            if (e.apiResponse.equals(BLOCKED_BOT_MESSAGE)) {
                LOG.warn("User ${message.chatId} stopped bot and should be removed from db")
//                userService.removeByTelegramId(message.chatId)
            } else {
                LOG.error("MessageOut sent is failed. API Response = [${e.apiResponse}]", e)
            }
            return BotApi.Send.Failed(message.chatId.toLong(), e)
        } catch (e: Exception) {
            LOG.error("MessageOut sending failed because of", e)
            return BotApi.Send.Failed(message.chatId.toLong(), TelegramApiException(e.message))
        }
    }

}