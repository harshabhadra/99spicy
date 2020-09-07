package com.a99Spicy.a99spicy.ui.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.a99Spicy.a99spicy.databinding.OrderListItemBinding
import com.a99Spicy.a99spicy.network.OrderResponse

class MyOrdersListAdapter(private val clickListener: MyOrderListItemClickListener) :
    ListAdapter<OrderResponse, MyOrdersListAdapter.MyOrdersListViewHolder>(
        MyOrdersListDiffUtilCallBack()
    ) {

    class MyOrdersListViewHolder private constructor(private val binding: OrderListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(order: OrderResponse, clickListener: MyOrderListItemClickListener) {
            binding.order = order
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): MyOrdersListViewHolder {
                val binding = OrderListItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return MyOrdersListViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyOrdersListViewHolder {
        return MyOrdersListViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyOrdersListViewHolder, position: Int) {
        val order = getItem(position)
        order?.let {
            holder.bind(order, clickListener)
        }
    }
}

class MyOrderListItemClickListener(val clickListener: (order: OrderResponse) -> Unit) {
    fun onMyOrderListItemClick(order: OrderResponse) = clickListener(order)
}

class MyOrdersListDiffUtilCallBack : DiffUtil.ItemCallback<OrderResponse>() {
    override fun areItemsTheSame(oldItem: OrderResponse, newItem: OrderResponse): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: OrderResponse, newItem: OrderResponse): Boolean {
        return oldItem.id == newItem.id
    }

}