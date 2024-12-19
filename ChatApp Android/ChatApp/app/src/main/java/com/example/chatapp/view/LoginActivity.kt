package com.example.chatapp.view

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.View.OnClickListener
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.chatapp.R
import com.example.chatapp.databinding.ActivityLoginBinding
import com.example.chatapp.view.viewModels.UserLoginViewModel
import com.google.gson.JsonObject


class LoginActivity : AppCompatActivity(), OnClickListener {

    private lateinit var binding : ActivityLoginBinding
    private lateinit var userLoginViewModel: UserLoginViewModel
    val TAG = "LoginActivity"
    lateinit var loadingDialog : Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@LoginActivity , R.layout.activity_login)
        init()
    }

    private fun init(){
        setOnClickListener()
        userLoginViewModel = UserLoginViewModel()
    }

    private fun setOnClickListener() {
        binding.apply {
            tvSignIn.setOnClickListener(this@LoginActivity)
            ivSignin.setOnClickListener(this@LoginActivity)
            tvCreateAccount.setOnClickListener(this@LoginActivity)
        }
    }

    override fun onClick(view: View?) {
        when(view!!.id){
            R.id.tvSignIn -> {
                showDialog()
                manageUserLogin()
            }
            R.id.ivSignin -> {
                showDialog()
                manageUserLogin()
            }
            R.id.tvCreateAccount -> {
                val intentRegistration = Intent(this@LoginActivity, RegistrationActivity::class.java)
                startActivity(intentRegistration)
            }
        }
    }

    private fun showDialog() {
        loadingDialog = Dialog(this@LoginActivity)
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

    private fun manageUserLogin() {
        val email = binding.etUsername.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        Log.e(TAG, "manageUserLogin: ${email}, ${password}", )

        binding.etUsername.clearFocus()
        binding.etPassword.clearFocus()

        if(email.isNullOrEmpty()){
            Handler(Looper.myLooper()!!).postDelayed({
                loadingDialog.hide()
                binding.etUsername.requestFocus()
            },2000)
        }else if (password.isNullOrEmpty()){
            Handler(Looper.myLooper()!!).postDelayed({
                loadingDialog.hide()
                binding.etPassword.requestFocus()
            },2000)
        }else{
            val jsonObjet = JsonObject()
            jsonObjet.addProperty("email", email)
            jsonObjet.addProperty("password", password)
            userLoginViewModel.postUserLogin(jsonObjet).observe(this@LoginActivity, Observer  {
//                Log.e(TAG, "manageUserLogin: ${it}", )
                Handler(Looper.myLooper()!!).postDelayed({
                    loadingDialog.hide()
                    val intentMainActivity = Intent(this@LoginActivity , MainActivity::class.java)
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

            userLoginViewModel.mutableLiveDataError.observe(this@LoginActivity, Observer { errorMessage ->
                Handler(Looper.myLooper()!!).postDelayed({
                    loadingDialog.hide()
                    Toast.makeText(this@LoginActivity, "Error: $errorMessage", Toast.LENGTH_SHORT).show()
                },2000)

            })
        }

    }

}