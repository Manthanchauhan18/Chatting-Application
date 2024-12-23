package com.example.chatapp.network

import android.util.Log
import com.example.chatapp.utils.Constants.SOCKET_URL
import com.google.gson.JsonObject
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject

class SocketIOManager(val messageReceivedListener: MessageReceivedListener, val deleteMessageListener: DeleteMessageListener) {

    private lateinit var socket: Socket
    val TAG = "SocketIOManager"

    init {
        // Initialize the Socket.IO client and connect to the server
        try {
            socket = IO.socket(SOCKET_URL) // Replace with your server URL
            socket.on(Socket.EVENT_CONNECT) {
                Log.e(TAG, "Connected to server")
            }
            socket.on(Socket.EVENT_DISCONNECT) {
                Log.e(TAG, "Disconnected from server")
            }
            socket.on("received_message") { args ->
                Log.e(TAG, "${args}: ", )
                // Handle incoming message
                if (args.isNotEmpty()) {
                    val data = args[0] as JSONObject
                    Log.e(TAG, "data: ${data}", )
                    messageReceivedListener.onMessageReceived(data)
                }
            }
            socket.on("message_deleted_by_opponent") { args ->
                Log.e(TAG, "${args}: ", )
                // Handle incoming message
                if (args.isNotEmpty()) {
                    val data = args[0] as JSONObject
                    Log.e(TAG, "data: ${data}", )
                    deleteMessageListener.onDeleteMessageListener(data)
                }
            }
//            socket.connect()
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing socket", e)
        }
    }

    fun connectSocket(){
        socket.connect()
    }

    fun loginUser(userId: String, username: String){
        val jsonObject = JSONObject()
        jsonObject.put("userId",userId)
        jsonObject.put("username",username)
        socket.emit("login",jsonObject)
    }

    // Send a message to the server
    fun sendMessage(__v: Int, _id: String, createdAt: String, from: String ,message: String, to: String, read: Boolean, updatedAt: String) {
        val jsonObject = JSONObject()
        jsonObject.put("from", from)
        jsonObject.put("to", to)
        jsonObject.put("message", message)
        jsonObject.put("__v", __v)
        jsonObject.put("_id", _id)
        jsonObject.put("createdAt", createdAt)
        jsonObject.put("updatedAt", updatedAt)
        jsonObject.put("read", read)
        Log.e(TAG, "sendMessage: ${jsonObject}", )
        socket.emit("send_message", jsonObject)
    }

    // Disconnect the socket connection
    fun disconnect() {
        socket.disconnect()
        Log.e(TAG, "Socket disconnected")
    }

    fun messageDeleted(from: String, to: String) {
        val jsonObject = JSONObject()
        jsonObject.put("from", from)
        jsonObject.put("to", to)
        socket.emit("message_deleted",jsonObject)
    }

    interface MessageReceivedListener {
        fun onMessageReceived(data: JSONObject)
    }
    interface DeleteMessageListener {
        fun onDeleteMessageListener(data: JSONObject)
    }

}