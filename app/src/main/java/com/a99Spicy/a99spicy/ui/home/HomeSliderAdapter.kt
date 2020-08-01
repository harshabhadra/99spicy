package com.a99Spicy.a99spicy.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.a99Spicy.a99spicy.R
import com.a99Spicy.a99spicy.domain.DomainBannerItem
import com.smarteist.autoimageslider.SliderViewAdapter
import com.squareup.picasso.Picasso

class HomeSliderAdapter(private val imageList: List<DomainBannerItem>) :
    SliderViewAdapter<HomeSliderAdapter.SliderViewHolder>() {

    class SliderViewHolder(itemView: View) : SliderViewAdapter.ViewHolder(itemView) {

        val sliderIv: ImageView = itemView.findViewById(R.id.slider_image)

        companion object {
            fun from(parent: ViewGroup): SliderViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.home_slider_item, parent, false)
                return SliderViewHolder(view)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup): SliderViewHolder {
        return SliderViewHolder.from(parent)
    }

    override fun getCount(): Int {
        return imageList.size
    }

    override fun onBindViewHolder(viewHolder: SliderViewHolder?, position: Int) {
        viewHolder?.let {
            if (imageList.isNotEmpty()) {

                val item = imageList[position]
                Picasso.get().load(item.image)
                    .centerCrop()
                    .fit()
                    .into(viewHolder.sliderIv)
            }
        }
    }
}