package com.example.chatapp.view

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import androidx.core.net.toUri
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.databinding.ActivityMainBinding
import com.example.chatapp.model.chat.Chat
import com.example.chatapp.model.user.UserResponseItem
import com.example.chatapp.network.SocketIOManager
import com.example.chatapp.utils.Constants
import com.example.chatapp.view.Adapter.ChatAdapter
import com.example.chatapp.view.Adapter.UserAdapter
import com.example.chatapp.view.viewModels.ChatViewModel
import com.example.chatapp.view.viewModels.UserLoginViewModel
import io.socket.client.On.Handle
import org.json.JSONObject

class MainActivity : AppCompatActivity(), OnClickListener, UserAdapter.ItemOnClickListener{
//    , SocketIOManager.MessageReceivedListener {

    private lateinit var binding: ActivityMainBinding
//    private lateinit var socketIOManager: SocketIOManager
    val TAG = "MainActivity"
    var userId = ""
    private lateinit var chatViewModel: ChatViewModel
    val chatUsersList = ArrayList<UserResponseItem>()
    lateinit var userAdapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@MainActivity, R.layout.activity_main)
        init()
    }

    private fun init(){
        binding.swiperefresh.isRefreshing = true
        chatViewModel = ChatViewModel()
        userAdapter = UserAdapter(this@MainActivity)

        val preferences = getSharedPreferences("LogedInPrefrance", MODE_PRIVATE)
        val userName = preferences.getString("userName", "")
        binding.tvUsername.text = userName

        userId = preferences.getString("userId", "")!!
        val userProfileImage = preferences.getString("userProfileImage", "")!!
        val fullUrl = "${Constants.BASE_URL}${userProfileImage}"
        Glide.with(this@MainActivity).load(fullUrl).placeholder(R.drawable.profile).into(binding.ivUserProfile)

        setOnClickListener()
        getChatUsers()


        binding.swiperefresh.setOnRefreshListener {
            getChatUsers()
        }

//        socketIOManager = SocketIOManager(this@MainActivity)
//        socketIOManager.loginUser(userId!!, userName!!)
    }

    private fun getChatUsers() {
        chatViewModel.getChatUsers(userId).observe(this@MainActivity){
            chatUsersList.clear()
            chatUsersList.addAll(it)
            setRecyclerview()
        }
    }

    private fun setRecyclerview() {
        Handler(Looper.myLooper()!!).postDelayed({
            binding.swiperefresh.isRefreshing = false
            userAdapter.setList(chatUsersList)
            binding.rvUsers.adapter = userAdapter
        },1000)
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

    override fun itemClick(user: UserResponseItem) {
        val intentChatActivity = Intent(this@MainActivity, ChatActivity::class.java)
        intentChatActivity.putExtra("user", user)
        startActivity(intentChatActivity)
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        socketIOManager.disconnect()
//    }

//    override fun onMessageReceived(data: JSONObject) {
//        Log.e(TAG, "onMessageReceived: ${data}", )
//    }

}