package com.example.chatapp.network

import com.example.chatapp.model.user.UserResponse
import com.example.chatapp.models.user.UserLogin
import com.google.gson.JsonObject
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

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

}