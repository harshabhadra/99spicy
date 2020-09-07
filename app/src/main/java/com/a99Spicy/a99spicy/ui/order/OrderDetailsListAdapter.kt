package com.a99Spicy.a99spicy.ui.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.a99Spicy.a99spicy.databinding.OrderDetailsListItemBinding
import com.a99Spicy.a99spicy.network.LineItem
import com.a99Spicy.a99spicy.network.ResponseLineItem

class OrderDetailsListAdapter:
    ListAdapter<ResponseLineItem, OrderDetailsListAdapter.OrderDetailsListViewHolder>(
        OrderDetailsListDiffUtilCallback()
    ) {

    class OrderDetailsListViewHolder private constructor(val binding: OrderDetailsListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(lineItem: ResponseLineItem) {
            binding.item = lineItem
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): OrderDetailsListViewHolder {
                val binding = OrderDetailsListItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return OrderDetailsListViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailsListViewHolder {
        return OrderDetailsListViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: OrderDetailsListViewHolder, position: Int) {
        val item = getItem(position)
        item?.let {
            holder.bind(it)
        }
    }
}

class OrderDetailsListDiffUtilCallback : DiffUtil.ItemCallback<ResponseLineItem>() {
    override fun areItemsTheSame(oldItem: ResponseLineItem, newItem: ResponseLineItem): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: ResponseLineItem, newItem: ResponseLineItem): Boolean {
        return oldItem.productId == newItem.productId
    }

}