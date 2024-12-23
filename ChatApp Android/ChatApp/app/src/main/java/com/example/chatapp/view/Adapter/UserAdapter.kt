package com.example.chatapp.view.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.databinding.ChatHomeRvBinding
import com.example.chatapp.model.chat.ChatResponseItem
import com.example.chatapp.model.user.UserResponseItem
import com.example.chatapp.utils.Constants

class UserAdapter(var itemOnClickListener: ItemOnClickListener, var itemOnLongClickListener: ItemOnLongClickListener, val from: String): RecyclerView.Adapter<UserAdapter.ViewHolder>() {


    var userList = ArrayList<UserResponseItem>()
    val TAG = "BlogsAdapter"
    private var selectedUsers = ArrayList<String>()

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var binding: ChatHomeRvBinding? = null
        init {
            binding = ChatHomeRvBinding.bind(view)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_home_rv, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder){
            with(userList[position]){
                if(from == "NewChatActivity"){
                    val fullUrl = "${Constants.BASE_URL}${this.profileImage}"
                    Glide.with(holder.itemView).load(fullUrl).placeholder(R.drawable.profile).into(binding!!.ivUserProfile)
                    binding!!.tvUserName.setText(this.fullname)
                    binding!!.tvStatus.setText(this.status)
                    binding!!.clUser.setOnClickListener{
                        itemOnClickListener.itemClick(this)
                    }
                }else{
                    val fullUrl = "${Constants.BASE_URL}${this.profileImage}"
                    Glide.with(holder.itemView).load(fullUrl).placeholder(R.drawable.profile).into(binding!!.ivUserProfile)
                    binding!!.tvUserName.setText(this.fullname)
                    binding!!.tvStatus.setText(this.lastMessage)
//                    Log.e(TAG, "onBindViewHolder: ${this.unreadMess}", )
                    if(this.unreadMessCount > 0){
                        binding!!.cvCounter.visibility = View.VISIBLE
                        binding!!.tvUnreadCount.text = this.unreadMessCount.toString()
                    }else{
                        binding!!.cvCounter.visibility = View.GONE
                    }
                    binding!!.clUser.setOnClickListener{
                        itemOnClickListener.itemClick(this)
                    }
                    binding!!.clUser.setOnLongClickListener {
                        selectedUsers.add(this._id)
                        itemOnLongClickListener.itemOnLongClickListener(selectedUsers)
                        notifyDataSetChanged()
                        true
                    }

                    if(selectedUsers.isNotEmpty()){
                        binding!!.clUser.setOnClickListener {
                            if(selectedUsers.isNotEmpty()) {
                                if (selectedUsers.contains(this._id)) {
                                    selectedUsers.remove(this._id)
                                } else {
                                    selectedUsers.add(this._id)
                                }
                                itemOnLongClickListener.itemOnLongClickListener(selectedUsers)
                                notifyDataSetChanged()
                            }
                        }

                    }
                    if (selectedUsers.contains(this._id)){
                        // You can change the background color or any UI element to indicate selection
                        binding!!.clUser.setBackgroundColor(holder.itemView.resources.getColor(R.color.selected_message_background))
                    } else {
                        binding!!.clUser.setBackgroundColor(holder.itemView.resources.getColor(R.color.default_message_background))
                    }
                }

            }
        }
    }

    fun setList(data: ArrayList<UserResponseItem>){
        if(userList.isNotEmpty()){
            userList.clear()
        }
        userList.addAll(data)
//        Log.e(TAG, "setList: ${userList}", )
        notifyDataSetChanged()
    }

    interface ItemOnClickListener {
        fun itemClick(user: UserResponseItem)
    }

    fun clearSelection() {
        selectedUsers.clear()
        notifyDataSetChanged()
    }

    fun isUserSelected(): Boolean {
        return selectedUsers.isEmpty()
    }

    interface ItemOnLongClickListener{
        fun itemOnLongClickListener(item: ArrayList<String>)
    }


}