package com.example.chatapp.view

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import com.example.chatapp.R
import com.example.chatapp.databinding.ActivityMainBinding
import com.example.chatapp.network.SocketIOManager

class MainActivity : AppCompatActivity(), OnClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var socketIOManager: SocketIOManager
    val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@MainActivity, R.layout.activity_main)
        init()
    }

    private fun init(){
        setOnClickListener()
        val preferences = getSharedPreferences("LogedInPrefrance", MODE_PRIVATE)
        val userName = preferences.getString("userName", "")
        val userId = preferences.getString("userId", "")

        binding.tvUsername.text = userName

        socketIOManager = SocketIOManager()
        socketIOManager.loginUser(userId!!, userName!!)
    }

    private fun setOnClickListener() {
        binding.apply {
            ivSideMenu.setOnClickListener(this@MainActivity)
            ivNewChat.setOnClickListener(this@MainActivity)

            miLogout.setOnClickListener(this@MainActivity)
            miProfile.setOnClickListener(this@MainActivity)
            miSetting.setOnClickListener(this@MainActivity)
        }
    }

    override fun onClick(view: View?) {
        when(view!!.id){
            R.id.ivSideMenu -> {
//                Log.e(TAG, "onClick: sidemenu clicked", )
                binding.drawerLayout.openDrawer(GravityCompat.START)
            }
            R.id.miLogout -> {
                val preferences = getSharedPreferences("LogedInPrefrance", MODE_PRIVATE)
                val editor = preferences.edit()
                editor.putBoolean("LoggedIn", false)
                editor.commit()
                val intentLoginScreen = Intent(this@MainActivity , LoginActivity::class.java)
                startActivity(intentLoginScreen)
                finish()
            }
            R.id.ivNewChat -> {
                val intentNewChatActivity = Intent(this@MainActivity , NewChatActivity::class.java)
                startActivity(intentNewChatActivity)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        socketIOManager.disconnect()
    }

}