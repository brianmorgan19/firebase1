package com.example.firebase

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.net.toUri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.userProfileChangeRequest
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth

    lateinit var capturedUri:Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        reg_btn.setOnClickListener {
            registerUser()
        }

        login_btn.setOnClickListener {
            loggedUser()
        }

        btn_update.setOnClickListener {
            updateProfile()
        }
    }

    private fun registerUser() {

        val email = reg_email.text.toString()
        val password = reg_password.text.toString()

        if(email.isNotEmpty() && password.isNotEmpty()){
            CoroutineScope(Dispatchers.IO).launch{
                try {
                    auth.createUserWithEmailAndPassword(email, password).await()
                    withContext(Dispatchers.Main){
                        loggedUser()
                    }
                }
                catch (e:Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_LONG).show()
                    }
                }
        }
        }
        else{
            Toast.makeText(this, "All fields should be filled", Toast.LENGTH_LONG).show()
        }
    }

    private fun loginUser() {

        val email = reg_email.text.toString()
        val password = reg_password.text.toString()

        if(email.isNotEmpty() && password.isNotEmpty()){
            CoroutineScope(Dispatchers.IO).launch{
                try {
                    auth.signInWithEmailAndPassword(email, password).await()
                    withContext(Dispatchers.Main){
                        loggedUser()
                    }
                }
                catch (e:Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
        else{
            Toast.makeText(this, "All fields should be filled", Toast.LENGTH_LONG).show()
        }
    }

    private fun updateProfile(){
        auth.currentUser?.let {
        val name = edTextUpdate.text.toString()
        val uri = Uri.parse("android.resource://$packageName/${R.drawable.ic_background_background}")
        val profileBuilder = UserProfileChangeRequest.Builder()
            .setDisplayName(name)
            .setPhotoUri(uri)
            .build()

        CoroutineScope(Dispatchers.IO).launch{
            try {
                it.updateProfile(profileBuilder)
                withContext(Dispatchers.Main){
                    loggedUser()
                }
            }
            catch (e:Exception){
                withContext(Dispatchers.Main){
                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        }
    }

    private fun loggedUser() {
        val user = auth.currentUser
        if(user == null){
            loggedText.text = "You are not logged"
        }
        else{
            edTextUpdate.setText(user.displayName)
            ivPhoto.setImageURI(user.photoUrl)
            loggedText.text = "You are logged"
        }
    }

}