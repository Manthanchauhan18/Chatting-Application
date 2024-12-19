package com.example.chatapp.view

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.View.OnClickListener
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.chatapp.R
import com.example.chatapp.databinding.ActivityRegistrationBinding
import com.example.chatapp.view.viewModels.UserLoginViewModel
import com.google.gson.JsonObject

class RegistrationActivity : AppCompatActivity(), OnClickListener {

    private lateinit var binding : ActivityRegistrationBinding
    private lateinit var userLoginViewModel: UserLoginViewModel

    val TAG = "RegistrationActivity"
    lateinit var loadingDialog : Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@RegistrationActivity, R.layout.activity_registration)
        init()
    }

    private fun init(){
        setOnClickListener()
        userLoginViewModel = UserLoginViewModel()
    }

    private fun setOnClickListener() {
        binding.apply {
            tvCreate.setOnClickListener(this@RegistrationActivity)
            ivCreate.setOnClickListener(this@RegistrationActivity)
        }
    }

    override fun onClick(view: View?) {
        when(view!!.id){
            R.id.tvCreate -> {
                showDialog()
                manageCreateUser()
            }
            R.id.ivCreate -> {
                showDialog()
                manageCreateUser()
            }
        }
    }

    private fun showDialog() {
        loadingDialog = Dialog(this@RegistrationActivity)
        val window: Window? = loadingDialog.window
        window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setGravity(Gravity.CENTER)
        loadingDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        loadingDialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        loadingDialog.setCancelable(false)
        loadingDialog.setContentView(R.layout.loading_dialogbox)
        loadingDialog.show()
    }

    private fun manageCreateUser() {
        val fullname = binding.etFullname.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val status = binding.etStatus.text.toString().trim()

        binding.etFullname.clearFocus()
        binding.etEmail.clearFocus()
        binding.etPassword.clearFocus()
        binding.etStatus.clearFocus()

        if(fullname.isNullOrEmpty()){
            Handler(Looper.myLooper()!!).postDelayed({
                loadingDialog.hide()
                binding.etFullname.requestFocus()
            },2000)
        }else if(email.isNullOrEmpty()){
            Handler(Looper.myLooper()!!).postDelayed({
                loadingDialog.hide()
                binding.etEmail.requestFocus()
            },2000)
        }else if(password.isNullOrEmpty()){
            Handler(Looper.myLooper()!!).postDelayed({
                loadingDialog.hide()
                binding.etPassword.requestFocus()
            },2000)
        }else{
            if(status.isNullOrEmpty()){
                val jsonObject = JsonObject()
                jsonObject.addProperty("fullname", fullname)
                jsonObject.addProperty("email", email)
                jsonObject.addProperty("password", password)
                apiCallForRegistration(jsonObject, email, password)
            }else{
                val jsonObject = JsonObject()
                jsonObject.addProperty("fullname", fullname)
                jsonObject.addProperty("email", email)
                jsonObject.addProperty("password", password)
                jsonObject.addProperty("status", status)
                apiCallForRegistration(jsonObject, email, password)

            }

        }

    }

    private fun apiCallForRegistration(jsonObject: JsonObject, email: String, password: String) {
        userLoginViewModel.postUserSignup(jsonObject).observe(this@RegistrationActivity){
            if(it.has("message")){
                val jsonObject = JsonObject()
                jsonObject.addProperty("email", email)
                jsonObject.addProperty("password", password)
                userLoginViewModel.postUserLogin(jsonObject).observe(this@RegistrationActivity, Observer  {
//                Log.e(TAG, "manageUserLogin: ${it}", )
                Handler(Looper.myLooper()!!).postDelayed({
                    loadingDialog.hide()
                    val intentMainActivity = Intent(this@RegistrationActivity , MainActivity::class.java)
                    startActivity(intentMainActivity)
                    finish()
                    val preferences = getSharedPreferences("LogedInPrefrance", MODE_PRIVATE)
                    val editor = preferences.edit()
                    editor.putBoolean("LoggedIn", true)
                    editor.putString("userId", it._id)
                    editor.putString("userName", it.fullname)
                    editor.commit()
                },2000)
            })
            }
        }

    }
}