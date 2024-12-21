package com.example.chatapp.view.viewModels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatapp.model.user.UserResponse
import com.example.chatapp.models.user.UserLogin
import com.example.chatapp.network.ApiInstance
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException

class UserLoginViewModel: ViewModel() {


    val mutableLiveData = MutableLiveData<UserLogin>()
    val mutableLiveDataError = MutableLiveData<String>()
    var compositeDisposable = CompositeDisposable()

    val TAG = "EmployeeVeiwModel"

    fun postUserLogin(jsonObject: JsonObject): MutableLiveData<UserLogin> {

        compositeDisposable.addAll(
            ApiInstance.apiInterface.postUserLogin(jsonObject)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ response -> onResponse(response)} , { failure -> onFailure(failure)})
        )

        return mutableLiveData as MutableLiveData<UserLogin>
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

    private fun onResponse(response: UserLogin) {
        Log.e(TAG, "onResponse: ${response}", )
        mutableLiveData.value = response
    }

    // ------------------------------------------------------------------------------------------------------- 
    
    val mutableLiveDataCreate = MutableLiveData<JsonObject>()

    fun postUserSignup(fullname: RequestBody, email: RequestBody, password: RequestBody, status: RequestBody, profileImage: MultipartBody.Part?): MutableLiveData<JsonObject> {

        Log.e(TAG, "postUserSignup: ${status}, ${profileImage}", )
        compositeDisposable.addAll(ApiInstance.apiInterface.postUserSignup(fullname, email, password, status, profileImage)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ response -> onResponseCreate(response)} , { failure -> onFailureCreate(failure)})
        )

        return mutableLiveDataCreate as MutableLiveData<JsonObject>
    }

    private fun onFailureCreate(failure: Throwable?) {
        Log.e(TAG, "onFailure: ${failure!!.message}", )

    }

    private fun onResponseCreate(response: JsonObject) {
//        Log.e(TAG, "onResponse: ${response}", )
        mutableLiveDataCreate.value = response
    }

    // -------------------------------------------------------------------------------------------------------

    val mutableLiveDataAllUser = MutableLiveData<UserResponse>()

    fun getAllUser(): MutableLiveData<UserResponse> {

        compositeDisposable.addAll(ApiInstance.apiInterface.getAllUser()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ response -> onResponseAllUser(response)} , { failure -> onFailureAllUser(failure)})
        )

        return mutableLiveDataAllUser as MutableLiveData<UserResponse>
    }

    private fun onFailureAllUser(failure: Throwable?) {
        Log.e(TAG, "onFailure: ${failure!!.message}", )

    }

    private fun onResponseAllUser(response: UserResponse) {
//        Log.e(TAG, "onResponse: ${response}", )
        mutableLiveDataAllUser.value = response
    }

}