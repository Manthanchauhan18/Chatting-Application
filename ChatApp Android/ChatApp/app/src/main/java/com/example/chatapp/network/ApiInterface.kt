package com.example.chatapp.network

import com.example.chatapp.model.chat.Chat
import com.example.chatapp.model.chat.ChatResponse
import com.example.chatapp.model.chat.ChatResponseItem
import com.example.chatapp.model.user.UserResponse
import com.example.chatapp.models.user.UserLogin
import com.google.gson.JsonObject
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiInterface {

    @POST("/user/login")
    fun postUserLogin(
        @Body jsonObject: JsonObject
    ): Observable<UserLogin>

    @Multipart
    @POST("/user/signup")
    fun postUserSignup(
        @Part("fullname") fullname: RequestBody,
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
        @Part("status") status: RequestBody,
        @Part profileImage: MultipartBody.Part?
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
    fun postChat(@Body jsonObject: JsonObject): Observable<ChatResponseItem>

//    @FormUrlEncoded
    @POST("chat/deleteMessages")
    fun postDeleteMessages(@Body messageList: List<ChatResponseItem>): Observable<JsonObject>

}