package com.example.chatapp.network

import com.example.chatapp.model.chat.Chat
import com.example.chatapp.model.chat.ChatResponse
import com.example.chatapp.model.user.UserResponse
import com.example.chatapp.models.user.UserLogin
import com.google.gson.JsonObject
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiInterface {

    @POST("/user/login")
    fun postUserLogin(
        @Body jsonObject: JsonObject
    ): Observable<UserLogin>

    @POST("/user/signup")
    fun postUserSignup(
        @Body jsonObject: JsonObject
    ): Observable<JsonObject>

    @GET("/user")
    fun getAllUser(): Observable<UserResponse>

    @GET("/chat/user/{userId}")
    fun getChatUser(@Path("userId") userId: String): Observable<UserResponse>

    @GET("/chat")
    fun getChat(
        @Query("from") from: String,
        @Query("to") to: String
    ): Observable<ChatResponse>

    @POST("/chat/create")
    fun postChat(@Body jsonObject: JsonObject): Observable<JsonObject>

}