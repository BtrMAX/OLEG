package ru.max.oleg.model.data.`class`

data class Chat(
    val ChatId: Int,
    val Messages: MutableList<Message>
)

data class Message(
    val SenderID: Int,
    val Time: ULong,
    val Text: String
)