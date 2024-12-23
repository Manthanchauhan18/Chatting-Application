package com.example.chatapp.model.user

import com.example.chatapp.model.chat.ChatResponseItem
import java.io.Serializable

class UserResponse : ArrayList<UserResponseItem>()

data class UserResponseItem(
    val __v: Int,
    val _id: String,
    val createdAt: String,
    val email: String,
    val fullname: String,
    val profileImage: String,
    val status: String,
    val unreadMessCount: Int,
    val unreadMess: ArrayList<ChatResponseItem>,
    val password: String,
    val salt: String,
    val updatedAt: String,
    val lastMessage: String
): Serializable