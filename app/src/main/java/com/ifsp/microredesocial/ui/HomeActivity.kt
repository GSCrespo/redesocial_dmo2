package com.ifsp.microredesocial.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.ifsp.microredesocial.R
import com.ifsp.microredesocial.adapter.PostAdapter
import com.ifsp.microredesocial.databinding.ActivityHomeBinding
import com.ifsp.microredesocial.databinding.ActivityLoginBinding
import com.ifsp.microredesocial.model.Post
import com.ifsp.microredesocial.util.Base64Converter

class HomeActivity : AppCompatActivity() {


    private val listaDePosts = mutableListOf<Post>()
    private var ultimoTimestamp: Timestamp? = null
    private var flagCarregando = false
    private lateinit var binding: ActivityHomeBinding
    private val firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var adapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }


    private fun setupListeners(){
        binding.buttonSair.setOnClickListener {
            signOut()
        }

        binding.buttonFeed.setOnClickListener {
            carregarPostsPaginados()
        }
        binding.buttonPost.setOnClickListener {
            launchPost()
        }
        binding.buttonProfile.setOnClickListener {
            launchProfile()
        }
        binding.buttonBuscar.setOnClickListener {
            val cidade = binding.editTextCidade.text.toString().trim()

            buscarPostsPorCidade(cidade)

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


    private fun carregarPostsPaginados() {

        if (flagCarregando) return
        flagCarregando = true

        val db = Firebase.firestore
        var query = db.collection("posts")
            .orderBy("data", Query.Direction.DESCENDING)
            .limit(5)

        if (ultimoTimestamp != null) {
            query = query.startAfter(ultimoTimestamp!!)
        }

        query.get().addOnSuccessListener { snapshot ->
            flagCarregando = false

            if (!snapshot.isEmpty) {
                val novosPosts = snapshot.map { doc ->
                    val descricao = doc.getString("descricao") ?: ""
                    val localizacao = doc.getString("localizacao") ?: ""
                    val imageString = doc.getString("imageString") ?: ""
                    val bitmap = Base64Converter.stringToBitmap(imageString)
                    Post(descricao, bitmap, localizacao)
                }

                ultimoTimestamp = snapshot.documents.last().getTimestamp("data")

                if (::adapter.isInitialized) {
                    adapter.adicionarPosts(novosPosts)
                } else {
                    listaDePosts.addAll(novosPosts)
                    adapter = PostAdapter(listaDePosts)
                    binding.recyclerView.layoutManager = LinearLayoutManager(this)
                    binding.recyclerView.adapter = adapter
                }
            } else {
                Toast.makeText(this, "Nenhum post encontrado", Toast.LENGTH_SHORT).show()
            }

        }.addOnFailureListener {
            flagCarregando = false
            Toast.makeText(this, "Erro ao carregar posts: ${it.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun buscarPostsPorCidade(cidade: String) {
        val db = Firebase.firestore
        db.collection("posts")
            .whereEqualTo("localizacao", cidade)
            .get()
            .addOnSuccessListener { result ->
                val posts = result.mapNotNull { doc ->
                    val descricao = doc.getString("descricao") ?: ""
                    val local = doc.getString("localizacao") ?: "Indefinida"
                    val imageString = doc.getString("imageString") ?: ""
                    val foto = Base64Converter.stringToBitmap(imageString)
                    Post(descricao, foto, local)
                }

                if (::adapter.isInitialized) {
                    adapter.limpar()
                    adapter.adicionarPosts(posts)
                } else {
                    listaDePosts.addAll(posts)
                    adapter = PostAdapter(listaDePosts)
                    binding.recyclerView.layoutManager = LinearLayoutManager(this)
                    binding.recyclerView.adapter = adapter
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao buscar posts", Toast.LENGTH_SHORT).show()
            }
    }

    private fun launchProfile(){
        startActivity(Intent(this, ProfileActivity::class.java))
        finish()
    }

    private fun launchPost(){
        startActivity(Intent(this, NewPostActivity::class.java))
        finish()
    }
}