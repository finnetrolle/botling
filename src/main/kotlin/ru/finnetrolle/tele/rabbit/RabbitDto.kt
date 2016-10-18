package ru.finnetrolle.tele.rabbit

/**
 * Created by maxsyachin on 19.10.16.
 */
data class ToSend(
        val method: String,
        val chatId: String,
        val text: String,
        val parseMode: String,
        val disableWebPagePreview: Boolean,
        val disableNotification: Boolean,
        val replyToMessageId: Int
)