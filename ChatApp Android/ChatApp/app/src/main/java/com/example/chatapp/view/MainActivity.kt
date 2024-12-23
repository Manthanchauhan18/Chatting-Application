package com.example.chatapp.view

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.PopupMenu
import androidx.core.net.toUri
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.databinding.ActivityMainBinding
import com.example.chatapp.model.chat.Chat
import com.example.chatapp.model.chat.ChatResponseItem
import com.example.chatapp.model.user.UserResponseItem
import com.example.chatapp.network.SocketIOManager
import com.example.chatapp.utils.Constants
import com.example.chatapp.view.Adapter.ChatAdapter
import com.example.chatapp.view.Adapter.UserAdapter
import com.example.chatapp.view.viewModels.ChatViewModel
import com.example.chatapp.view.viewModels.UserLoginViewModel
import io.socket.client.On.Handle
import org.json.JSONObject

class MainActivity : AppCompatActivity(), OnClickListener, UserAdapter.ItemOnClickListener, UserAdapter.ItemOnLongClickListener{
//    , SocketIOManager.MessageReceivedListener {

    private lateinit var binding: ActivityMainBinding
//    private lateinit var socketIOManager: SocketIOManager
    val TAG = "MainActivity"
    var userId = ""
    private lateinit var chatViewModel: ChatViewModel
    val chatUsersList = ArrayList<UserResponseItem>()
    lateinit var userAdapter: UserAdapter
    val selectedUsersList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@MainActivity, R.layout.activity_main)
        init()
    }

    private fun init(){
        binding.swiperefresh.isRefreshing = true
        chatViewModel = ChatViewModel()
        userAdapter = UserAdapter(this@MainActivity, this@MainActivity, "MainActivity")

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
            ivMenu.setOnClickListener(this@MainActivity)

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
            R.id.ivNewChat -> {
                val intentNewChatActivity = Intent(this@MainActivity , NewChatActivity::class.java)
                startActivity(intentNewChatActivity)
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
            R.id.miProfile -> {
                val intentProfile = Intent(this@MainActivity, ProfileActivity::class.java)
                startActivity(intentProfile)
            }

            R.id.ivMenu -> {
                showMenuPopUp(binding.ivMenu)
            }

        }
    }

    private fun showMenuPopUp(view: View) {
        val popupMenu = PopupMenu(this@MainActivity, view)
        menuInflater.inflate(R.menu.user_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menuDelete -> {
                    if(!userAdapter.isUserSelected()) {
                        deleteMessagesByuser()
                    }
                    true
                }
                else -> false
            }
        }

        // Show the popup menu
        popupMenu.show()
    }

    private fun deleteMessagesByuser() {
        if(!selectedUsersList.isEmpty()) {
            chatViewModel.deleteMessagesByUser(userId, selectedUsersList)
                .observe(this@MainActivity) {
                    Log.e(TAG, "deleteMessagesByuser: ${it}",)
                    if (it.has("message")) {
                        getChatUsers()
                        userAdapter.clearSelection()
                    }

                }
        }
    }

    override fun itemClick(user: UserResponseItem) {
        val intentChatActivity = Intent(this@MainActivity, ChatActivity::class.java)
        intentChatActivity.putExtra("user", user)
        startActivity(intentChatActivity)
    }

    override fun itemOnLongClickListener(item: ArrayList<String>) {
        Log.e(TAG, "itemOnLongClickListener: ${item}")
        selectedUsersList.clear()
        selectedUsersList.addAll(item)
        if(!selectedUsersList.isEmpty()){
            binding.ivMenu.visibility = View.VISIBLE
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
//        Log.e(TAG, "onBackPressed: ${userAdapter.isMessageSelected()}")
        if (!userAdapter.isUserSelected()) {
            userAdapter.clearSelection()
            binding.ivMenu.visibility = View.GONE
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        getChatUsers()
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        socketIOManager.disconnect()
//    }

//    override fun onMessageReceived(data: JSONObject) {
//        Log.e(TAG, "onMessageReceived: ${data}", )
//    }

}