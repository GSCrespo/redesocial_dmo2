package com.ifsp.microredesocial.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.ifsp.microredesocial.R
import com.ifsp.microredesocial.adapter.PostAdapter
import com.ifsp.microredesocial.databinding.ActivityHomeBinding
import com.ifsp.microredesocial.databinding.ActivityLoginBinding
import com.ifsp.microredesocial.model.Post
import com.ifsp.microredesocial.util.Base64Converter

class HomeActivity : AppCompatActivity() {

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
            loadFeed()
        }
        binding.buttonPost.setOnClickListener {
            launchPost()
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

    private fun loadFeed(){

        val db = Firebase.firestore

        db.collection("posts").get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    val document = task.result
                    val posts = ArrayList<Post>()
                    for (document in document.documents) {
                        val descricao = document.data!!["descricao"].toString()
                        val localizacao = document.getString("localizacao") ?: "Indefinido"
                        val imageString = document.data!!["imageString"].toString()
                        val bitmap = Base64Converter.stringToBitmap(imageString)
                        posts.add(Post(descricao, bitmap,localizacao))
                    }
                    adapter = PostAdapter(posts.toTypedArray())
                    binding.recyclerView.layoutManager = LinearLayoutManager(this)
                    binding.recyclerView.adapter = adapter
                }
                else{
                    Toast.makeText(this,"erro ao carregar post",Toast.LENGTH_LONG).show()

                }
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