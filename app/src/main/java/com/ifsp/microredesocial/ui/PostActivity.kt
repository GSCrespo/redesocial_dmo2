package com.ifsp.microredesocial.ui


import android.graphics.Bitmap
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.privacysandbox.tools.core.model.Method
import com.android.volley.Request
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.ifsp.microredesocial.databinding.ActivityPostBinding

class PostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val queue = Volley.newRequestQueue(this)
        val url = "http://10.105.68.94:8080/posts/1"
        val jsonRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                binding.postDescricao.text = response.getString("descricao")
                val queue = Volley.newRequestQueue(this)
                val urlImage = "http://10.105.68.94:8080/images/" +
                        response.getString("foto")
                val imageRequest = ImageRequest(urlImage,
                    { response ->
                        binding.logoProfile.setImageBitmap(response)
                    },
                    0, 0, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.RGB_565,
                    { error ->
                        error.printStackTrace()
                    })
                queue.add(imageRequest)
            },
            { error ->
                error.printStackTrace()
            }
        )
        queue.add(jsonRequest)
    }
}