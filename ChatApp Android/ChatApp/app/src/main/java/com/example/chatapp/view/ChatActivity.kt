package com.example.chatapp.view

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.databinding.ActivityChatBinding
import com.example.chatapp.model.chat.ChatResponseItem
import com.example.chatapp.model.user.UserResponseItem
import com.example.chatapp.network.SocketIOManager
import com.example.chatapp.utils.Constants
import com.example.chatapp.view.Adapter.ChatAdapter
import com.example.chatapp.view.viewModels.ChatViewModel
import com.google.gson.JsonObject
import org.json.JSONObject


class ChatActivity : AppCompatActivity(), OnClickListener, SocketIOManager.MessageReceivedListener,
    ChatAdapter.ItemOnClickListener , SocketIOManager.DeleteMessageListener{

    private lateinit var binding: ActivityChatBinding
    private var intentData: UserResponseItem?= null
    private lateinit var socketIOManager: SocketIOManager
    lateinit var chatAdapter: ChatAdapter
    lateinit var chatViewModel: ChatViewModel
    var userId = ""

    val chatList = ArrayList<ChatResponseItem>()
    val selectedMessagesList = ArrayList<ChatResponseItem>()

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

        val preferences = getSharedPreferences("LogedInPrefrance", MODE_PRIVATE)
        val userName = preferences.getString("userName", "")
        userId = preferences.getString("userId", "")!!

        val fullUrl = "${Constants.BASE_URL}${intentData!!.profileImage}"
        Glide.with(this@ChatActivity).load(fullUrl).placeholder(R.drawable.profile).into(binding.ivUserProfile)

        socketIOManager = SocketIOManager(this@ChatActivity, this@ChatActivity)
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
            chatList.clear()
            chatList.addAll(it)
            setRecyclerview()
        }
    }

    private fun setOnClickListener() {
        binding.apply {
            ivBackArrow.setOnClickListener(this@ChatActivity)
            ivSend.setOnClickListener(this@ChatActivity)
            ivMenu.setOnClickListener(this@ChatActivity)
        }
    }

    override fun onClick(view: View?) {
        when(view!!.id){
            R.id.ivBackArrow -> {
                onBackPressedDispatcher.onBackPressed()
            }
            R.id.ivSend -> {
                if(binding.etMessage.text.toString().isNotEmpty()){
                    sendMessage()
                }
            }
            R.id.ivMenu -> {
                showMenuPopUp(binding.ivMenu)
            }
        }
    }

    private fun showMenuPopUp(view: View) {
        val popupMenu = PopupMenu(this@ChatActivity, view)
        menuInflater.inflate(R.menu.chat_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menuCopy -> {
                    copyTextFromSelectedMess()
                    true
                }
                R.id.menuDelete -> {
                    if(!chatAdapter.isMessageSelected()) {
                        deleteMessages()
                    }
                    true
                }
                else -> false
            }
        }

        // Show the popup menu
        popupMenu.show()
    }

    private fun deleteMessages() {
        if(!selectedMessagesList.isEmpty()){
            chatViewModel.deleteMessages(selectedMessagesList).observe(this@ChatActivity){
                Log.e(TAG, "deleteMessages: ${it}", )
                if(it.has("message")){
                    socketIOManager.messageDeleted(userId,intentData!!._id)
                    chatAdapter.clearSelection()
                    getChat()
                }
            }
        }

    }

    private fun copyTextFromSelectedMess() {
        if(!chatAdapter.isMessageSelected()){
            var selectMess = ""
            for(chat in selectedMessagesList){
                selectMess += chat.message
                selectMess += "\n\n"
            }
            Log.e(TAG, "copyTextFromSelectedMess: ${selectMess}", )
            val clipboard: ClipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("copy", selectMess.trim())
            Log.e(TAG, "copyTextFromSelectedMess: ${clip}", )
            clipboard.setPrimaryClip(clip)
            chatAdapter.clearSelection()
        }
    }

    private fun sendMessage() {
        binding.etMessage.clearFocus()
        val message = binding.etMessage.text.toString().trim()
        val jsonObject = JsonObject()
        jsonObject.addProperty("to", intentData!!._id)
        jsonObject.addProperty("from", userId)
        jsonObject.addProperty("message", message)
        chatViewModel.sendChat(jsonObject).observe(this@ChatActivity){
//            socketIOManager.sendMessage(message, userId!!, intentData!!._id)
            socketIOManager.sendMessage(it.__v, it._id, it.createdAt, it.from, it.message, it.to, it.updatedAt)
            val chat = ChatResponseItem(it.__v, it._id, it.createdAt, it.from, it.message, it.to, it.updatedAt)
            addToList(chat)
            binding.etMessage.setText("")
        }
    }

    override fun onMessageReceived(data: JSONObject) {
        val from = data.getString("from")
        val message = data.getString("message")
        val __v = data.getInt("__v")
        val _id = data.getString("_id")
        val to = data.getString("to")
        val createdAt = data.getString("createdAt")
        val updatedAt = data.getString("updatedAt")
        if(intentData!!._id == from){
            val chat = ChatResponseItem(__v, _id, createdAt, from, message, userId, updatedAt)
            Log.e(TAG, "onMessageReceived: ${chat}", )
            runOnUiThread {
                addToList(chat)
            }
        }
    }

    private fun addToList(chat: ChatResponseItem) {
        if (!chatList.contains(chat)) {
            chatList.add(chat)
            setRecyclerview()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        socketIOManager.disconnect()
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        Log.e(TAG, "onBackPressed: ${chatAdapter.isMessageSelected()}")
        if (!chatAdapter.isMessageSelected()) {
            chatAdapter.clearSelection()
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun itemOnLongClickListener(item: ArrayList<ChatResponseItem>) {
        Log.e(TAG, "itemOnLongClickListener: ${item}")
        selectedMessagesList.clear()
        selectedMessagesList.addAll(item)
    }

    override fun onDeleteMessageListener(data: JSONObject) {
        val from = data.getString("from")
        if(intentData!!._id == from){
            runOnUiThread {
                getChat()
            }
        }
    }

}