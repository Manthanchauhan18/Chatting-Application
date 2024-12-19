package com.example.chatapp.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.chatapp.R
import com.example.chatapp.databinding.ActivitySplashBinding


class SplashActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@SplashActivity, R.layout.activity_splash)
        init()
    }

    private fun init(){
        val preferences = getSharedPreferences("LogedInPrefrance", MODE_PRIVATE)
        val checkUserLogin = preferences.getBoolean("LoggedIn", false)

        if(checkUserLogin){
            Handler(Looper.myLooper()!!).postDelayed({
                val intentMainActivity = Intent(this@SplashActivity , MainActivity::class.java)
                startActivity(intentMainActivity)
                finish()
            },2000)
        }else{
            Handler(Looper.myLooper()!!).postDelayed({
                val intentLogin = Intent(this@SplashActivity , LoginActivity::class.java)
                startActivity(intentLogin)
                finish()
            },2000)
        }


    }

}