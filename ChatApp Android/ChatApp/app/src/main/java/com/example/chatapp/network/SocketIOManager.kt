package com.example.chatapp.network

import android.util.Log
import com.example.chatapp.utils.Constants.SOCKET_URL
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject
import java.net.URI

class SocketIOManager {

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
                    Log.e(TAG, "Received message: $data")
                }
            }
            socket.connect()
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing socket", e)
        }
    }

    fun loginUser(userId: String, username: String){
        val jsonObject = JSONObject()
        jsonObject.put("userId",userId)
        jsonObject.put("username",username)
        socket.emit("login",jsonObject)
    }

    // Send a message to the server
    fun sendMessage(message: String, from: String, to: String) {
        val jsonObject = JSONObject()
        jsonObject.put("from", from)
        jsonObject.put("to", to)
        jsonObject.put("message", message)
        socket.emit("send_message", jsonObject)
    }

    // Disconnect the socket connection
    fun disconnect() {
        socket.disconnect()
        Log.e(TAG, "Socket disconnected")
    }

}