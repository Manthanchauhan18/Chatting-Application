package com.example.chatapp.view.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.databinding.ChatHomeRvBinding
import com.example.chatapp.model.user.UserResponseItem

class UserAdapter(var itemOnClickListener: ItemOnClickListener): RecyclerView.Adapter<UserAdapter.ViewHolder>() {


    var userList = ArrayList<UserResponseItem>()
    val TAG = "BlogsAdapter"

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
//                val fullUrl = "${Constants.BASE_URL}${this.blogImage}"
//                Glide.with(holder.itemView.context)
//                    .load(fullUrl)
//                    .placeholder(R.drawable.ic_launcher_background)
//                    .into(binding!!.ivBlog);
                binding!!.tvUserName.setText(this.fullname)
                binding!!.tvStatus.setText(this.status)
                binding!!.clUser.setOnClickListener{
                    itemOnClickListener.itemClick(this)
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


}