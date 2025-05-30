package com.ifsp.microredesocial.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.ifsp.microredesocial.R
import com.ifsp.microredesocial.databinding.ActivityProfileBinding
import com.ifsp.microredesocial.util.Base64Converter

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private val firebaseAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userEmail = FirebaseAuth.getInstance().currentUser?.email

        if (userEmail != null) {
            Firebase.firestore.collection("usuarios").document(userEmail).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val base64Foto = document.getString("FotoPerfil")
                        if (!base64Foto.isNullOrEmpty()) {
                            val bitmap = Base64Converter.stringToBitmap(base64Foto)
                            if (bitmap != null) {
                                binding.logoProfile.setImageBitmap(bitmap)
                            }
                        }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao carregar foto de perfil", Toast.LENGTH_SHORT).show()
                }
        }

        setupListeners()
    }

    private fun setupListeners(){

        val galeria = registerForActivityResult(
            ActivityResultContracts.PickVisualMedia()){
                uri ->
            if (uri != null){
                binding.logoProfile.setImageURI(uri)
            }else{
                Toast.makeText(this,"Nenhuma foto foi selecionada",Toast.LENGTH_LONG).show()
            }
        }

        binding.buttonAlterarFoto.setOnClickListener {
            galeria.launch(
                PickVisualMediaRequest(
                ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }


        binding.buttonSalvar.setOnClickListener {

            if (firebaseAuth.currentUser != null){
                if(binding.textAlterarSenha.text.toString().isNotBlank()){
                    salvarDados()
                }else{
                    atualizarDadosParciais()
                }

            }else{
                Toast.makeText(this,"é preciso estar logado no sistema",Toast.LENGTH_LONG).show()
            }
        }

        binding.buttonVoltar.setOnClickListener {
            launchHome()
        }
    }


    private fun atualizarDadosParciais(){
        val db = Firebase.firestore
        val email = firebaseAuth.currentUser!!.email.toString()
        val username = binding.textNameUser.text.toString()
        val nomeCompleto = binding.textNomeCompleto.text.toString()
        val novaSenha = binding.textAlterarSenha.text.toString()

        val drawable = binding.logoProfile.drawable ?: getDrawable(R.drawable.empty_profile)
        val fotoPerfilString = Base64Converter.drawableToString(drawable!!)


        val dados = hashMapOf(
            "NomeCompleto" to nomeCompleto,
            "Username" to username,
            "password" to novaSenha,
            "FotoPerfil" to fotoPerfilString
        )
        db.collection("usuarios").document(email)
            .set(dados)
            .addOnSuccessListener {
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
    }


    private fun salvarDados(){

        val db = Firebase.firestore
        val firebaseAuth = FirebaseAuth.getInstance()

        val email = firebaseAuth.currentUser!!.email.toString()
        val username = binding.textNameUser.text.toString()
        val nomeCompleto = binding.textNomeCompleto.text.toString()
        val novaSenha = binding.textAlterarSenha.text.toString()

        val drawable = binding.logoProfile.drawable ?: getDrawable(R.drawable.empty_profile)
        val fotoPerfilString = Base64Converter.drawableToString(drawable!!)



        if (novaSenha.length < 6) {
            Toast.makeText(this, "A nova senha deve conter pelo menos 6 caracteres", Toast.LENGTH_LONG).show()
            return
        }else {
            firebaseAuth.currentUser?.updatePassword(novaSenha)?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Senha alterada com sucesso!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(
                        this,
                        "Erro ao alterar senha: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        val dados = hashMapOf(
            "NomeCompleto" to nomeCompleto,
            "Username" to username,
            "password" to novaSenha,
            "FotoPerfil" to fotoPerfilString
        )
        db.collection("usuarios").document(email)
            .set(dados)
            .addOnSuccessListener {
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }

    }


    private fun launchHome(){
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}