package com.example.chatapp.view

import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.View.OnClickListener
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.databinding.ActivityProfileBinding
import com.example.chatapp.model.user.UserResponseItem
import com.example.chatapp.utils.Constants
import com.example.chatapp.view.viewModels.UserLoginViewModel
import com.google.gson.JsonObject
import java.io.FileNotFoundException
import java.io.InputStream

class ProfileActivity : AppCompatActivity(), OnClickListener {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var userLoginViewModel: UserLoginViewModel
    var userId = ""
    val TAG = "ProfileActivity"
    var selectedImageUri: Uri? = null
    var currentUser: UserResponseItem? = null
    lateinit var loadingDialog : Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@ProfileActivity, R.layout.activity_profile)
        init()
    }
    private fun init(){
        userLoginViewModel = UserLoginViewModel()
        val preferences = getSharedPreferences("LogedInPrefrance", MODE_PRIVATE)
        userId = preferences.getString("userId", "")!!

        setOnClickListener()
        getUserData()
    }

    private fun getUserData() {

        userLoginViewModel.getUserById(userId).observe(this@ProfileActivity){
            binding.apply {
                currentUser = it
                val fullUrl = "${Constants.BASE_URL}${it.profileImage}"
                Glide.with(this@ProfileActivity).load(fullUrl).placeholder(R.drawable.profile).into(ivUserProfile)

                etEmail.setHint(it.email)
                etStatus.setText(it.status)
                etFullname.setText(it.fullname)
            }
        }
    }

    private fun setOnClickListener() {
        binding.apply {
            cvUpdateProfile.setOnClickListener(this@ProfileActivity)
            ivEditProfile.setOnClickListener(this@ProfileActivity)
            cvChangePassword.setOnClickListener(this@ProfileActivity)
        }
    }

    private fun showDialog() {
        loadingDialog = Dialog(this@ProfileActivity)
        val window: Window? = loadingDialog.window
        window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setGravity(Gravity.CENTER)
        loadingDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        loadingDialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        loadingDialog.setCancelable(false)
        loadingDialog.setContentView(R.layout.loading_dialogbox)
        loadingDialog.show()
    }

    override fun onClick(view: View?) {
        when(view!!.id){
            R.id.cvUpdateProfile -> {
                showDialog()
                binding.etFullname.clearFocus()
                binding.etStatus.clearFocus()
                updateUserProfile()
            }
            R.id.ivEditProfile -> {
                binding.etFullname.clearFocus()
                binding.etEmail.clearFocus()
                binding.etStatus.clearFocus()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                        val permissions = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        requestPermissions(permissions, 100)
                    } else{
                        selectImageFromGallery()
                    }
                }else{
                    selectImageFromGallery()
                }
            }
            R.id.cvChangePassword -> {
                val intentChangePassword = Intent(this@ProfileActivity, ChangePasswordActivity::class.java)
                startActivity(intentChangePassword)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            100 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    selectImageFromGallery()
                } else {
                }
            }
        }


    }

    private fun selectImageFromGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            1000 -> {
//                loadingDialog.hide()
                selectedImageUri= data?.data
//                if(selectedImageUri != null) binding.ivChecked.visibility = View.VISIBLE
                Log.e(TAG, "onActivityResult: ${selectedImageUri}", )
            }
        }
    }

    fun getInputStreamFromUri(uri: Uri): InputStream? {
        return try {
            contentResolver.openInputStream(uri)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            null
        }
    }

    private fun updateUserProfile() {
        val fullname = binding.etFullname.text.toString().trim()
        val status = binding.etStatus.text.toString().trim()
        if(fullname.equals(currentUser!!.fullname) && status.equals(currentUser!!.status)){
            Handler(Looper.myLooper()!!).postDelayed({
                loadingDialog.hide()
                Toast.makeText(this@ProfileActivity, "please change the values which you want to update", Toast.LENGTH_SHORT).show()
            },2000)
        }else{
            if(!fullname.equals(currentUser!!.fullname) && !status.equals(currentUser!!.status)){
                val jsonObject = JsonObject()
                jsonObject.addProperty("fullname", fullname)
                jsonObject.addProperty("status", status)
                updateUser(jsonObject)
            }else if(fullname.equals(currentUser!!.fullname) && !status.equals(currentUser!!.status)){
                val jsonObject = JsonObject()
                jsonObject.addProperty("status", status)
                updateUser(jsonObject)
            }else if(!fullname.equals(currentUser!!.fullname) && status.equals(currentUser!!.status)){
                val jsonObject = JsonObject()
                jsonObject.addProperty("fullname", fullname)
                updateUser(jsonObject)
            }
        }
    }

    private fun updateUser(jsonObject: JsonObject){
        userLoginViewModel.updateUser(userId, jsonObject).observe(this@ProfileActivity){
            Handler(Looper.myLooper()!!).postDelayed({
                if(it.has("message")){
                    loadingDialog.hide()
                    Toast.makeText(this@ProfileActivity, "User Updated Successfuly", Toast.LENGTH_SHORT).show()
                }
            },2000)
        }
    }

}