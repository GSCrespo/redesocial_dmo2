package com.ifsp.microredesocial.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ifsp.microredesocial.R
import com.ifsp.microredesocial.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners(){

        binding.buttonAlterarFoto.setOnClickListener {
            val galeria = registerForActivityResult(
                ActivityResultContracts.PickVisualMedia()){
                    uri ->
                if (uri != null){
                    binding.logoProfile.setImageURI(uri)
                }else{
                    Toast.makeText(this,"Nenhuma foto foi selecionada",Toast.LENGTH_LONG).show()
                }
            }
            galeria.launch(
                PickVisualMediaRequest(
                ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }
    }



}