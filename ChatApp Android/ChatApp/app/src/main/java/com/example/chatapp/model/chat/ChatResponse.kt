package com.example.chatapp.model.chat

import java.io.Serializable

class ChatResponse : ArrayList<ChatResponseItem>()

data class ChatResponseItem(
    val __v: Int,
    val _id: String,
    val createdAt: String,
    val from: String,
    val message: String,
    val to: String,
    val read: Boolean,
    val updatedAt: String
): Serializable