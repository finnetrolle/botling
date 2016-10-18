package ru.finnetrolle.tele.rabbit

/**
 * Created by maxsyachin on 19.10.16.
 */
data class ToSend(
        var method: String,
        var chatId: String,
        var text: String,
        var parseMode: String,
        var disableWebPagePreview: Boolean,
        var disableNotification: Boolean,
        var replyToMessageId: Int
)