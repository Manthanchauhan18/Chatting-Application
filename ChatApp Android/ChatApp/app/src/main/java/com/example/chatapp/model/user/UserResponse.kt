package com.example.chatapp.model.user

import java.io.Serializable

class UserResponse : ArrayList<UserResponseItem>()

data class UserResponseItem(
    val __v: Int,
    val _id: String,
    val createdAt: String,
    val email: String,
    val fullname: String,
    val status: String,
    val password: String,
    val salt: String,
    val updatedAt: String
): Serializable