package com.example.chatapp.view

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
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
    lateinit var loadingDialog : Dialog

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
                showDialog()
                binding.etOldPassword.clearFocus()
                binding.etNewPassword.clearFocus()
                binding.etReEnterNewPassword.clearFocus()
                updatePassword()
            }
        }
    }

    private fun showDialog() {
        loadingDialog = Dialog(this@ChangePasswordActivity)
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

    private fun updatePassword() {
        val oldPassword = binding.etOldPassword.text.toString().trim()
        val newPassowrd = binding.etNewPassword.text.toString().trim()
        val newReEnterPassword = binding.etReEnterNewPassword.text.toString().trim()

        if(!newPassowrd.equals(newReEnterPassword)){
            Handler(Looper.myLooper()!!).postDelayed({
                loadingDialog.hide()
                Toast.makeText(this@ChangePasswordActivity, "new password and re-entered password was not matched", Toast.LENGTH_SHORT).show()
            },2000)
        }else{
            val jsonObject = JsonObject()
            jsonObject.addProperty("userId", userId)
            jsonObject.addProperty("oldPassword", oldPassword)
            jsonObject.addProperty("newPassword", newPassowrd)
            userLoginViewModel.updatePassword(jsonObject).observe(this@ChangePasswordActivity){
                Handler(Looper.myLooper()!!).postDelayed({
                    if(it.has("message")){
                        loadingDialog.hide()
                        Toast.makeText(this@ChangePasswordActivity, "${it.get("message")}", Toast.LENGTH_SHORT).show()
                    }
                },2000)
            }
        }
    }

}