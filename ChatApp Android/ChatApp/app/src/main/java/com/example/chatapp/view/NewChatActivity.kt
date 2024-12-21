package com.example.chatapp.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import androidx.databinding.DataBindingUtil
import com.example.chatapp.R
import com.example.chatapp.databinding.ActivityNewChatBinding
import com.example.chatapp.model.user.UserResponseItem
import com.example.chatapp.view.Adapter.UserAdapter
import com.example.chatapp.view.viewModels.UserLoginViewModel

class NewChatActivity : AppCompatActivity(), OnClickListener, UserAdapter.ItemOnClickListener {

    private lateinit var binding: ActivityNewChatBinding
    private lateinit var userLoginViewModel: UserLoginViewModel
    val userList = ArrayList<UserResponseItem>()
    private lateinit var userAdapter: UserAdapter

    val TAG = "NewChatActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@NewChatActivity, R.layout.activity_new_chat)
        init()
    }

    private fun init(){
        userLoginViewModel = UserLoginViewModel()
        userAdapter = UserAdapter(this@NewChatActivity)
        binding.swiperefresh.isRefreshing = true
        setOnClickListener()
        getAllUser()
        binding.swiperefresh.setOnRefreshListener {
            getAllUser()
        }
    }

    private fun getAllUser() {
        userLoginViewModel.getAllUser().observe(this@NewChatActivity){
            userList.clear()
            userList.addAll(it)
            setRecyclerView()
        }
    }

    private fun setRecyclerView() {
        Handler(Looper.myLooper()!!).postDelayed({
            binding.swiperefresh.isRefreshing = false

            val preferences = getSharedPreferences("LogedInPrefrance", MODE_PRIVATE)
            val userId = preferences.getString("userId","")

            val filteredList = userList.filter { it._id != userId } as ArrayList<UserResponseItem>

            userAdapter.setList(filteredList)
            binding.rvUsers.adapter = userAdapter
        },2000)

    }

    private fun setOnClickListener() {
        binding.apply {
            
        }
    }

    override fun onClick(view: View?) {

    }

    override fun itemClick(user: UserResponseItem) {
//        Log.e(TAG, "itemClick: ${user}", )
        val intentChatActivity = Intent(this@NewChatActivity, ChatActivity::class.java)
        intentChatActivity.putExtra("user", user)
        startActivity(intentChatActivity)
    }


}