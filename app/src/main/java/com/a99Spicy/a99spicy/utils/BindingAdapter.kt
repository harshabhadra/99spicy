package com.a99Spicy.a99spicy.utils

import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.a99Spicy.a99spicy.R
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("catImage")
fun setCategoryImage(iv: ImageView, img: Int?) {

    img?.let {
        Picasso.get().load(img).into(iv)
    }
}

@BindingAdapter("setImage")
fun setImage(iv: ImageView, imgUrl: String?) {
    imgUrl?.let {
        Timber.e("Img url: $it")
        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
        Glide.with(iv.context)
            .load(imgUri)
            .placeholder(R.drawable.app_logo)
            .error(R.drawable.app_logo)
            .centerCrop()
            .into(iv)
    }
}

@BindingAdapter("setDateText")
fun setDateText(tv: TextView,input:String) {
    tv.text = getToday()
}

fun getToday(): String {
    val date = Calendar.getInstance().time
    return date.toString("E, dd MMM yyyy")
}

private fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
    val formatter = SimpleDateFormat(format, locale)
    return formatter.format(this)
}