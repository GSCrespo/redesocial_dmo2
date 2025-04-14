package com.ifsp.microredesocial.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.ifsp.microredesocial.R
import com.ifsp.microredesocial.databinding.ActivityHomeBinding
import com.ifsp.microredesocial.databinding.ActivityLoginBinding
import com.ifsp.microredesocial.util.Base64Converter

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val db = Firebase.firestore
        val email = firebaseAuth.currentUser!!.email.toString()

        db.collection("usuarios").document(email).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    val document = task.result
                    val imageString = document.data!!["FotoPerfil"].toString()
                    val bitmap = Base64Converter.stringToBitmap(imageString)
                    binding.logoProfile.setImageBitmap(bitmap)
                    binding.textUsername.text = document.data!!["Username"].toString()
                    binding.textNomeCompleto.text = document.data!!["NomeCompleto"].toString()
                }
            }

        setupListeners()
    }


    private fun setupListeners(){
        binding.buttonLogout.setOnClickListener {
            signOut()
        }

        binding.buttonProfile.setOnClickListener {
            launchProfile()
        }
    }

    private fun signOut(){

        firebaseAuth.signOut()
        launchLogin()
    }


    private fun launchLogin(){
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun launchProfile(){
        startActivity(Intent(this, ProfileActivity::class.java))
        finish()
    }
}