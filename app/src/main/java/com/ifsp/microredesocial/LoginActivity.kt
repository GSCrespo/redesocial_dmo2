package com.ifsp.microredesocial

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
        setContentView(R.layout.activity_login)
        binding = ActivityLoginBinding.inflate(layoutInflater)

        setupListeners()
    }

    private fun setupListeners() {

        binding.btnSend.setOnClickListener{
        val email = binding.txtName.toString()
        val password = binding.txtSenha.toString()

            if(binding.txtName.toString().isNotEmpty()){

                if(binding.txtSenha.toString().isNotEmpty()){

                    firebaseAuth
                        .signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                startActivity(Intent(this, HomeActivity::class.java))
                                finish()
                            } else {
                                Toast.makeText(this, "Erro no login", Toast.LENGTH_LONG).show()
                            }
                        }

                }

            }

        }
    }

}