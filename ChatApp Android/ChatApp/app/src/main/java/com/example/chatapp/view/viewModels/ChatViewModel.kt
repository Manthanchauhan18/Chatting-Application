package com.example.chatapp.view.viewModels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatapp.model.chat.ChatResponse
import com.example.chatapp.model.user.UserResponse
import com.example.chatapp.models.user.UserLogin
import com.example.chatapp.network.ApiInstance
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException

class ChatViewModel: ViewModel() {

    val mutableLiveData = MutableLiveData<ChatResponse>()
    val mutableLiveDataError = MutableLiveData<String>()
    var compositeDisposable = CompositeDisposable()

    val TAG = "EmployeeVeiwModel"

    fun getChat(from: String, to: String): MutableLiveData<ChatResponse> {

        compositeDisposable.addAll(
            ApiInstance.apiInterface.getChat(from, to)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ response -> onResponse(response)} , { failure -> onFailure(failure)})
        )

        return mutableLiveData as MutableLiveData<ChatResponse>
    }

    private fun onFailure(failure: Throwable) {
        Log.e(TAG, "onFailure: ", failure)
        if (failure is HttpException) {
            try {
                val errorResponse = failure.response()?.errorBody()?.string()
                val errorJsonObject = Gson().fromJson(errorResponse, JsonObject::class.java)

                if (errorJsonObject.has("error")) {
                    val errorMessage = errorJsonObject.get("error").asString
                    mutableLiveDataError.value = errorMessage
                } else {
                    mutableLiveDataError.value = "An unexpected error occurred."
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing error response: ", e)
                mutableLiveDataError.value = "An unexpected error occurred."
            }
        } else {
            mutableLiveDataError.value = "Network error or an unexpected problem occurred."
        }
    }

    private fun onResponse(response: ChatResponse) {
        Log.e(TAG, "onResponse: ${response}", )
        mutableLiveData.value = response
    }


    //--------------------------------------------------------------------------------------------------------------

    val mutableLiveDataSend = MutableLiveData<JsonObject>()
    val mutableLiveDataErrorSend = MutableLiveData<String>()

    fun sendChat(jsonObject: JsonObject): MutableLiveData<JsonObject> {

        compositeDisposable.addAll(ApiInstance.apiInterface.postChat(jsonObject)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ response -> onResponseSend(response)} , { failure -> onFailureSend(failure)})
        )

        return mutableLiveDataSend as MutableLiveData<JsonObject>
    }

    private fun onFailureSend(failure: Throwable) {
        Log.e(TAG, "onFailure: ", failure)
        if (failure is HttpException) {
            try {
                val errorResponse = failure.response()?.errorBody()?.string()
                val errorJsonObject = Gson().fromJson(errorResponse, JsonObject::class.java)

                if (errorJsonObject.has("error")) {
                    val errorMessage = errorJsonObject.get("error").asString
                    mutableLiveDataErrorSend.value = errorMessage
                } else {
                    mutableLiveDataErrorSend.value = "An unexpected error occurred."
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing error response: ", e)
                mutableLiveDataErrorSend.value = "An unexpected error occurred."
            }
        } else {
            mutableLiveDataErrorSend.value = "Network error or an unexpected problem occurred."
        }
    }

    private fun onResponseSend(response: JsonObject) {
        Log.e(TAG, "onResponse: ${response}", )
        mutableLiveDataSend.value = response
    }


//--------------------------------------------------------------------------------------------------------------

    val mutableLiveDataChatUser = MutableLiveData<UserResponse>()
    val mutableLiveDataErrorChatUser = MutableLiveData<String>()

    fun getChatUsers(userId: String): MutableLiveData<UserResponse> {

        compositeDisposable.addAll(ApiInstance.apiInterface.getChatUser(userId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ response -> onResponseChatUser(response)} , { failure -> onFailureChatUser(failure)})
        )

        return mutableLiveDataChatUser as MutableLiveData<UserResponse>
    }

    private fun onFailureChatUser(failure: Throwable) {
        Log.e(TAG, "onFailure: ", failure)
        if (failure is HttpException) {
            try {
                val errorResponse = failure.response()?.errorBody()?.string()
                val errorJsonObject = Gson().fromJson(errorResponse, JsonObject::class.java)

                if (errorJsonObject.has("error")) {
                    val errorMessage = errorJsonObject.get("error").asString
                    mutableLiveDataErrorChatUser.value = errorMessage
                } else {
                    mutableLiveDataErrorChatUser.value = "An unexpected error occurred."
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing error response: ", e)
                mutableLiveDataErrorChatUser.value = "An unexpected error occurred."
            }
        } else {
            mutableLiveDataErrorChatUser.value = "Network error or an unexpected problem occurred."
        }
    }

    private fun onResponseChatUser(response: UserResponse) {
        Log.e(TAG, "onResponse: ${response}", )
        mutableLiveDataChatUser.value = response
    }

}