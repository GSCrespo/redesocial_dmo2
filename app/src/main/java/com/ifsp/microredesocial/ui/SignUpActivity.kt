package com.ifsp.microredesocial.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.ifsp.microredesocial.R
import com.ifsp.microredesocial.databinding.ActivityLoginBinding
import com.ifsp.microredesocial.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }


    private fun setupListeners(){

        binding.buttonSignUp.setOnClickListener{
            handleSignUp()
        }

        binding.buttonHome.setOnClickListener {
            launchHome()
        }
    }



    private fun handleSignUp(){
        val email = binding.textEmail.text.toString()
        val password = binding.textPassword.text.toString()
        val confirmPassword = binding.textPasswordConfirm.text.toString()

        if(email.isNotEmpty() && password.isNotEmpty() && confirmPassword == password){
            firebaseAuth
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        launchProfile()
                    } else {
                        Toast.makeText(this, "Erro na validação do cadastro", Toast.LENGTH_LONG).show()
                    }
                }
        }else{
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_LONG).show()
        }
    }

    private fun launchHome(){
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    private fun launchProfile(){
        startActivity(Intent(this, ProfileActivity::class.java))
        finish()
    }

}