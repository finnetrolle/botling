package ru.finnetrolle.tele.telegram.api

import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.exceptions.TelegramApiException

/**
 * Created by maxsyachin on 18.10.16.
 */
interface BotApi {

    interface Send {
        data class Success(val chatId: Long, val spend: Long) : Send
        data class Failed(val chatId: Long, val exception: TelegramApiException) : Send
    }

    open fun send(message: SendMessage): Send

}