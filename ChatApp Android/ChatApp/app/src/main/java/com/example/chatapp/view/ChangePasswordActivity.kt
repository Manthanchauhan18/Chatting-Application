package com.example.chatapp.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import androidx.databinding.DataBindingUtil
import com.example.chatapp.R
import com.example.chatapp.databinding.ActivityChangePasswordBinding
import com.example.chatapp.view.viewModels.UserLoginViewModel
import com.google.gson.JsonObject

class ChangePasswordActivity : AppCompatActivity(), OnClickListener {

    private lateinit var binding: ActivityChangePasswordBinding
    val TAG = "ChangePasswordActivity"
    private lateinit var userLoginViewModel: UserLoginViewModel
    var userId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@ChangePasswordActivity, R.layout.activity_change_password)
        init()
    }

    private fun init(){
        userLoginViewModel = UserLoginViewModel()
        val preferences = getSharedPreferences("LogedInPrefrance", MODE_PRIVATE)
        userId = preferences.getString("userId", "")!!
        setOnClickListener()
    }

    private fun setOnClickListener() {
        binding.apply {
            cvChangePassword.setOnClickListener(this@ChangePasswordActivity)
        }
    }

    override fun onClick(view: View?) {
        when(view!!.id){
            R.id.cvChangePassword -> {
                updatePassword()
            }
        }
    }

    private fun updatePassword() {
        val oldPassword = binding.etOldPassword.text.toString().trim()
        val newPassowrd = binding.etNewPassword.text.toString().trim()
        val newReEnterPassword = binding.etReEnterNewPassword.text.toString().trim()

        if(!newPassowrd.equals(newReEnterPassword)){
            Log.e(TAG, "updatePassword: newpassword was not matched with reentered password", )
        }else{
            val jsonObject = JsonObject()
            jsonObject.addProperty("userId", userId)
            jsonObject.addProperty("oldPassword", oldPassword)
            jsonObject.addProperty("newPassword", newPassowrd)
            userLoginViewModel.updatePassword(jsonObject).observe(this@ChangePasswordActivity){
                Log.e(TAG, "updatePassword: ${it}", )
            }
        }
    }

}