package com.example.chatapp.view

import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.View.OnClickListener
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.chatapp.R
import com.example.chatapp.databinding.ActivityRegistrationBinding
import com.example.chatapp.view.viewModels.UserLoginViewModel
import com.google.gson.JsonObject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.FileNotFoundException
import java.io.InputStream

class RegistrationActivity : AppCompatActivity(), OnClickListener {

    private lateinit var binding : ActivityRegistrationBinding
    private lateinit var userLoginViewModel: UserLoginViewModel

    val TAG = "RegistrationActivity"
    lateinit var loadingDialog : Dialog
    var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@RegistrationActivity, R.layout.activity_registration)
        init()
    }

    private fun init(){
        setOnClickListener()
        userLoginViewModel = UserLoginViewModel()
    }

    private fun setOnClickListener() {
        binding.apply {
            tvCreate.setOnClickListener(this@RegistrationActivity)
            ivCreate.setOnClickListener(this@RegistrationActivity)
            cvSelectImage.setOnClickListener(this@RegistrationActivity)
        }
    }

    override fun onClick(view: View?) {
        when(view!!.id){
            R.id.tvCreate -> {
                showDialog()
                manageCreateUser()
            }
            R.id.ivCreate -> {
                showDialog()
                manageCreateUser()
            }
            R.id.cvSelectImage -> {
                binding.etFullname.clearFocus()
                binding.etEmail.clearFocus()
                binding.etStatus.clearFocus()
                binding.etPassword.clearFocus()
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

        }
    }

    private fun showDialog() {
        loadingDialog = Dialog(this@RegistrationActivity)
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

    private fun manageCreateUser() {
        val fullname = binding.etFullname.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val status = binding.etStatus.text.toString().trim()

        binding.etFullname.clearFocus()
        binding.etEmail.clearFocus()
        binding.etPassword.clearFocus()
        binding.etStatus.clearFocus()

        if(fullname.isNullOrEmpty()){
            Handler(Looper.myLooper()!!).postDelayed({
                loadingDialog.hide()
                binding.etFullname.requestFocus()
            },2000)
        }else if(email.isNullOrEmpty()){
            Handler(Looper.myLooper()!!).postDelayed({
                loadingDialog.hide()
                binding.etEmail.requestFocus()
            },2000)
        }else if(password.isNullOrEmpty()){
            Handler(Looper.myLooper()!!).postDelayed({
                loadingDialog.hide()
                binding.etPassword.requestFocus()
            },2000)
        }else{

            val fullnameBody = RequestBody.create("text/plain".toMediaTypeOrNull(), fullname)
            val emailBody = RequestBody.create("text/plain".toMediaTypeOrNull(), email)
            val passwordBody = RequestBody.create("text/plain".toMediaTypeOrNull(), password)
            val statusBody = RequestBody.create("text/plain".toMediaTypeOrNull(), status)

            Log.e(TAG, "manageCreateUser: ${selectedImageUri}", )
            if(selectedImageUri == null){
                apiCallForRegistration(fullnameBody, emailBody, passwordBody, statusBody, null, email, password)
            } else{
                val filePath2 = getInputStreamFromUri(selectedImageUri!!)
                val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), filePath2!!.readBytes())
                val imagePart = MultipartBody.Part.createFormData("profileImage", "image.jpg", requestBody)
//                Log.e(TAG, "manageBlogPosting: ${imagePart}", )
                apiCallForRegistration(fullnameBody, emailBody, passwordBody, statusBody, imagePart, email, password)
            }

        }

    }

    private fun apiCallForRegistration(fullname: RequestBody, email: RequestBody, password: RequestBody, status: RequestBody, profileImage: MultipartBody.Part?, email2: String, password2: String) {
        
        userLoginViewModel.postUserSignup(fullname, email, password, status, profileImage).observe(this@RegistrationActivity){
            if(it.has("message")){
                val jsonObject = JsonObject()
                jsonObject.addProperty("email", email2)
                jsonObject.addProperty("password", password2)
                userLoginViewModel.postUserLogin(jsonObject).observe(this@RegistrationActivity, Observer  {
//                Log.e(TAG, "manageUserLogin: ${it}", )
                Handler(Looper.myLooper()!!).postDelayed({
                    loadingDialog.hide()
                    val intentMainActivity = Intent(this@RegistrationActivity , MainActivity::class.java)
                    startActivity(intentMainActivity)
                    finish()
                    val preferences = getSharedPreferences("LogedInPrefrance", MODE_PRIVATE)
                    val editor = preferences.edit()
                    editor.putBoolean("LoggedIn", true)
                    editor.putString("userId", it._id)
                    editor.putString("userName", it.fullname)
                    editor.putString("userProfileImage", it.profileImage)
                    editor.commit()
                },2000)
            })
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

    fun getRealPathFromURI(contentUri: Uri): String? {
        Log.e(TAG, "getRealPathFromURI: ${contentUri}")
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = contentResolver.query(contentUri, projection, null, null, null)

        cursor?.use {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            if (it.moveToFirst()) {
                return it.getString(columnIndex)
            }
        }
        return null
    }

    fun getInputStreamFromUri(uri: Uri): InputStream? {
        return try {
            contentResolver.openInputStream(uri)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            null
        }
    }

}