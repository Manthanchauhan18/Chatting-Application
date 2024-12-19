package com.example.chatapp.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import androidx.databinding.DataBindingUtil
import com.example.chatapp.R
import com.example.chatapp.databinding.ActivityChatBinding
import com.example.chatapp.model.chat.Chat
import com.example.chatapp.model.user.UserResponseItem
import com.example.chatapp.network.SocketIOManager
import com.google.gson.JsonObject
import io.socket.client.Socket
import org.json.JSONObject

class ChatActivity : AppCompatActivity(), OnClickListener {

    private lateinit var binding: ActivityChatBinding
    private var intentData: UserResponseItem?= null
    private lateinit var socketIOManager: SocketIOManager
//    private lateinit var socket: Socket
    val chatList = ArrayList<Chat>()

    val TAG = "ChatActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@ChatActivity, R.layout.activity_chat)
        init()
    }

    private fun init(){
        setOnClickListener()
        intentData = intent.getSerializableExtra("user") as UserResponseItem?
        binding.tvUsername.setText(intentData!!.fullname)
        socketIOManager = SocketIOManager()
//        socket.on("received_message") { args ->
//            if (args.isNotEmpty()) {
//                val data = args[0] as JSONObject
//                Log.d("SocketIO", "Received message: $data")
//            }
//        }
    }

    private fun setOnClickListener() {
        binding.apply {
            ivBackArrow.setOnClickListener(this@ChatActivity)
            ivSend.setOnClickListener(this@ChatActivity)
        }
    }

    override fun onClick(view: View?) {
        when(view!!.id){
            R.id.ivBackArrow -> {
                onBackPressedDispatcher.onBackPressed()
            }
            R.id.ivSend -> {
                sendMessage()
            }
        }
    }

    private fun sendMessage() {
        val preferences = getSharedPreferences("LogedInPrefrance", MODE_PRIVATE)
        val userId = preferences.getString("userId", "")!!
        val message = binding.etMessage.text.toString().trim()
        socketIOManager.sendMessage(message, userId!!, intentData!!._id)
        val chat = Chat(message, "12:30", "right")
        chatList.add(chat)
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        socketIOManager.disconnect()
//    }

}