package com.example.chatapp.view.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.databinding.MessageLayoutBinding
import com.example.chatapp.model.chat.Chat
import com.example.chatapp.model.chat.ChatResponseItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

class ChatAdapter(var itemOnClickListener: ItemOnClickListener): RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    val messageList = ArrayList<ChatResponseItem>()
    var userId = ""
    val TAG = "ChatAdapter"

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        var binding: MessageLayoutBinding? = null
        init {
            binding = MessageLayoutBinding.bind(view)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.message_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder){
            with(messageList[position]){
                if(this.from == userId){
                    binding!!.cvReceive.visibility = View.GONE
                    binding!!.cvSend.visibility = View.VISIBLE
                    binding!!.tvMessage.setText(this.message)
                    if(this.createdAt == ""){
                        val time = dateConvertNull(this.createdAt)
                        binding!!.tvTime.setText(time)
                    }else{
                        val time = dateConvert(this.createdAt)
                        binding!!.tvTime.setText(time)
                    }

                }else {
                    binding!!.cvReceive.visibility = View.VISIBLE
                    binding!!.cvSend.visibility = View.GONE
                    binding!!.tvReceiveMessage.setText(this.message)
                    if(this.createdAt == ""){
                        val time = dateConvertNull(this.createdAt)
                        binding!!.tvTime.setText(time)
                    }else{
                        val time = dateConvert(this.createdAt)
                        binding!!.tvTime.setText(time)
                    }

                }
            }
        }
    }

    private fun dateConvertNull(createdAt: String): String {
        val date = Date()
        val sdf = SimpleDateFormat("hh:mm a")
        val timeInAmPm: String = sdf.format(date)
        return timeInAmPm
    }

    fun dateConvert(createdAt: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = inputFormat.parse(createdAt)
        val outputFormat = SimpleDateFormat("hh:mm a")
        val timeInAmPm = outputFormat.format(date)
        return timeInAmPm
    }

    fun setList(data: ArrayList<ChatResponseItem>, userId: String){
        if(messageList.isNotEmpty()) messageList.clear()
        messageList.addAll(data)
        this.userId = userId
        notifyDataSetChanged()
    }

    interface ItemOnClickListener{
        fun itemOnClickListener(item: Chat)
    }

}