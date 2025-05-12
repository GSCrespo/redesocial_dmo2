package com.ifsp.microredesocial.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.ifsp.microredesocial.R
import com.ifsp.microredesocial.adapter.PostAdapter
import com.ifsp.microredesocial.databinding.ActivityNewPostBinding
import com.ifsp.microredesocial.model.Post
import com.ifsp.microredesocial.util.Base64Converter
import com.ifsp.microredesocial.util.LocalizacaoHelper

class NewPostActivity : AppCompatActivity() , LocalizacaoHelper.Callback {


    private lateinit var binding: ActivityNewPostBinding
    private val db = Firebase.firestore
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNewPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }



    private fun setupListeners(){

        val galeria = registerForActivityResult(
            ActivityResultContracts.PickVisualMedia()){
                uri ->
            if (uri != null){
                binding.logoProfile.setImageURI(uri)
            }else{
                Toast.makeText(this,"Nenhuma foto foi selecionada", Toast.LENGTH_LONG).show()
            }
        }

        binding.buttonAdicionarFoto.setOnClickListener {
            galeria.launch(
                PickVisualMediaRequest(
                ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }

        binding.buttonGetLocalizacao.setOnClickListener {
            solicitarLocalizacao()
        }

        binding.buttonEnviarPost.setOnClickListener {
            salvarPost()
        }

        binding.buttonVoltar.setOnClickListener {
            launchHome()
        }
    }


    private fun salvarPost(){

        val emailUser = firebaseAuth.currentUser!!.email


        if (emailUser != null) {
            val docRef = db.collection("usuarios").document(emailUser)
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val autor = document.getString("Username") ?: "Desconhecido"
                        val fotoPostString = Base64Converter.drawableToString(binding.logoProfile.drawable) ?: null
                        val descricao = binding.textDescricao.text.toString()
                        val localizacaoInput = binding.txtCidade.text.toString()
                        var localizacao: String? = null


                        if (localizacaoInput.isNotBlank()) {
                            localizacao = localizacaoInput
                        }


                        val dados = hashMapOf(
                            "autor" to autor,
                            "data" to FieldValue.serverTimestamp(),
                            "descricao" to descricao,
                            "imageString" to fotoPostString
                        )

                        if (localizacao != null) {
                            dados["localizacao"] = localizacao
                        }

                        db.collection("posts")
                            .add(dados)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Post salvo com sucesso!", Toast.LENGTH_SHORT).show()
                                launchHome()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Erro ao salvar post: ${e.message}", Toast.LENGTH_LONG).show()
                            }

                    } else {
                        Toast.makeText(this, "Usuario não encontrado no banco", Toast.LENGTH_LONG).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erro ao buscar dados do usuário: ${e.message}", Toast.LENGTH_LONG).show()
                }
        } else {
            Toast.makeText(this, "Usuario não está logado", Toast.LENGTH_LONG).show()
        }


    }



    private fun solicitarLocalizacao() {

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            val localizacaoHelper = LocalizacaoHelper(applicationContext)
            localizacaoHelper.obterLocalizacaoAtual(this)
        }
    }

    override fun onLocalizacaoRecebida(endereco: Address, latitude: Double, longitude: Double)
    {

        runOnUiThread {
            var infos = endereco.subAdminArea
            binding.txtCidade.text = infos

        }
    }
    override fun onErro(mensagem: String) {
        System.out.println(mensagem)
    }
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults:
        IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions,
            grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            solicitarLocalizacao()
        } else {
            Toast.makeText(this, "Permissão de localização negada",
                Toast.LENGTH_SHORT).show()
        }
    }

    private fun launchHome(){
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}