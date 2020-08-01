package com.a99Spicy.a99spicy.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso

@BindingAdapter("catImage")
fun setCategoryImage(iv:ImageView, img:Int?){

    img?.let {
        Picasso.get().load(img).into(iv)
    }
}