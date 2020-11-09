package com.a99Spicy.a99spicy.ui.coupon

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.a99Spicy.a99spicy.database.DatabaseCoupon
import com.a99Spicy.a99spicy.databinding.CouponListItemBinding

class CouponListAdapter(private val clickListener: CouponListItemClickListener) :
    ListAdapter<DatabaseCoupon, CouponListAdapter.CouponListViewHolder>
        (CouponListDiffUtilCallback()) {

    class CouponListViewHolder private constructor(private val binding: CouponListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(coupon: DatabaseCoupon, clickListener: CouponListItemClickListener) {
            binding.coupon = coupon
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): CouponListViewHolder {
                val binding = CouponListItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return CouponListViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CouponListViewHolder {
        return CouponListViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: CouponListViewHolder, position: Int) {
        val coupon = getItem(position)
        coupon?.let {
            holder.bind(it, clickListener)
        }
    }
}

class CouponListItemClickListener(val clickListener: (coupon: DatabaseCoupon) -> Unit) {
    fun onCouponClickListener(coupon: DatabaseCoupon) = clickListener(coupon)
}

class CouponListDiffUtilCallback : DiffUtil.ItemCallback<DatabaseCoupon>() {
    override fun areItemsTheSame(oldItem: DatabaseCoupon, newItem: DatabaseCoupon): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: DatabaseCoupon, newItem: DatabaseCoupon): Boolean {
        return oldItem.id == newItem.id
    }

}