package com.ifsp.microredesocial.model

import android.graphics.Bitmap

class Post(private val descricao: String, private val foto: Bitmap) {

    fun getDescricao() : String{
        return descricao
    }
    fun getFoto() : Bitmap{
        return foto
    }

}