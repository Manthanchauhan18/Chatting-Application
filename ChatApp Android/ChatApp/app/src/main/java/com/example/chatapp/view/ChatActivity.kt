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
import com.example.chatapp.model.chat.ChatResponseItem
import com.example.chatapp.model.user.UserResponseItem
import com.example.chatapp.network.SocketIOManager
import com.example.chatapp.view.Adapter.ChatAdapter
import com.example.chatapp.view.viewModels.ChatViewModel
import com.google.gson.JsonObject
import org.json.JSONObject

class ChatActivity : AppCompatActivity(), OnClickListener, SocketIOManager.MessageReceivedListener,
    ChatAdapter.ItemOnClickListener {

    private lateinit var binding: ActivityChatBinding
    private var intentData: UserResponseItem?= null
    private lateinit var socketIOManager: SocketIOManager
    lateinit var chatAdapter: ChatAdapter
    lateinit var chatViewModel: ChatViewModel
    var userId = ""

    val chatList = ArrayList<ChatResponseItem>()

    val TAG = "ChatActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@ChatActivity, R.layout.activity_chat)
        init()
    }

    private fun init(){
        chatAdapter = ChatAdapter(this@ChatActivity)
        chatViewModel = ChatViewModel()
        setOnClickListener()

        intentData = intent.getSerializableExtra("user") as UserResponseItem?
        binding.tvUsername.setText(intentData!!.fullname)
        socketIOManager = SocketIOManager(this@ChatActivity)

        val preferences = getSharedPreferences("LogedInPrefrance", MODE_PRIVATE)
        val userName = preferences.getString("userName", "")
        userId = preferences.getString("userId", "")!!

        socketIOManager = SocketIOManager(this@ChatActivity)
        socketIOManager.connectSocket()
        socketIOManager.loginUser(userId!!, userName!!)

        getChat()
        setRecyclerview()

    }

    private fun setRecyclerview() {
        chatAdapter.setList(chatList,userId)
        binding.rvChat.adapter = chatAdapter
    }

    private fun getChat() {
        chatViewModel.getChat(userId!!, intentData!!._id).observe(this@ChatActivity){
            chatList.addAll(it)
            setRecyclerview()
        }
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
        binding.etMessage.clearFocus()
        val message = binding.etMessage.text.toString().trim()
        socketIOManager.sendMessage(message, userId!!, intentData!!._id)
        val jsonObject = JsonObject()
        jsonObject.addProperty("to", intentData!!._id)
        jsonObject.addProperty("from", userId)
        jsonObject.addProperty("message", message)
        chatViewModel.sendChat(jsonObject).observe(this@ChatActivity){
//            Log.e(TAG, "sendMessage: ${it}", )
        }

        val chat = ChatResponseItem(0, "", "", userId, message, intentData!!._id, "")
//        Log.e(TAG, "sendMessage: ${chat}", )
        addToList(chat)

        binding.etMessage.setText("")
    }

    override fun onMessageReceived(data: JSONObject) {
//        Log.e(TAG, "onMessageReceived: ${data}", )
        val from = data.getString("from")
        val message = data.getString("message")
//        Log.e(TAG, "onMessageReceived: ${from}, ${message}", )
        if(intentData!!._id == from){
            val chat = ChatResponseItem(0, "", "", from, message, userId, "")
            runOnUiThread {
                addToList(chat)
            }
        }
    }

    private fun addToList(chat: ChatResponseItem) {
        chatList.add(chat)
        setRecyclerview()
    }

    override fun itemOnClickListener(item: Chat) {
//        Log.e(TAG, "itemOnClickListener: ${item}", )
    }


    override fun onDestroy() {
        super.onDestroy()
        socketIOManager.disconnect()
    }

}