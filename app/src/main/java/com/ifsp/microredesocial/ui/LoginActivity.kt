package com.ifsp.microredesocial.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.ifsp.microredesocial.databinding.ActivityLoginBinding


class LoginActivity : AppCompatActivity() {

    private lateinit var binding:  ActivityLoginBinding
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)




        setupListeners()
        checkUser()
    }

    private fun setupListeners() {

        binding.buttonLogin.setOnClickListener{
            handleLogin()
        }

        binding.buttonCadastrar.setOnClickListener {
            launchSignUp()
        }
    }

    private fun checkUser(){

        if(firebaseAuth.currentUser != null){
            launhcHome()
        }
    }


    private fun handleLogin(){
        val email = binding.textEmail.text.toString()
        val password = binding.textPassword.text.toString()

        if(email.isNotEmpty() && password.isNotEmpty()){
            firebaseAuth
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        launchProfile()
                    } else {
                        Toast.makeText(this, "Erro na validação do login", Toast.LENGTH_LONG).show()
                    }
                }
        }else{
            Toast.makeText(this, "Preencha os dois campos", Toast.LENGTH_LONG).show()
        }
    }

    private fun launchProfile(){
        startActivity(Intent(this, ProfileActivity::class.java))
        finish()
    }

    private fun launchSignUp(){
        startActivity(Intent(this, SignUpActivity::class.java))
        finish()
    }
    private fun launhcHome(){
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

}