package com.example.chatapp.models.user

data class UserLogin(
    val __v: Int,
    val _id: String,
    val createdAt: String,
    val email: String,
    val status: String,
    val fullname: String,
    val password: String,
    val profileImage: String,
    val salt: String,
    val updatedAt: String
)