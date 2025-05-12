package com.ifsp.microredesocial.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ifsp.microredesocial.R
import com.ifsp.microredesocial.model.Post

class PostAdapter (private val posts: MutableList<Post>) :
    RecyclerView.Adapter<PostAdapter.ViewHolder>(){

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
    {
        val imgPost : ImageView = view.findViewById(R.id.imgPost)
        val txtDescricao : TextView = view.findViewById(R.id.descricao)
        val localizacao : TextView = view.findViewById(R.id.localizacao)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.post_item, parent, false)
        return ViewHolder(view)
    }
    override fun getItemCount(): Int {
        return posts.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtDescricao.text = posts[position].getDescricao()
        holder.localizacao.text = posts[position].getLocalizacao()
        holder.imgPost.setImageBitmap(posts[position].getFoto())
    }

    fun adicionarPosts(novos: List<Post>) {
        val start = posts.size
        posts.addAll(novos)
        notifyItemRangeInserted(start, novos.size)
    }
}
