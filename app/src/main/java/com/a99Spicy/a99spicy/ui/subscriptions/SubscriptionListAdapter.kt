package com.a99Spicy.a99spicy.ui.subscriptions

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.a99Spicy.a99spicy.databinding.SubscribeListItemBinding
import com.a99Spicy.a99spicy.network.Subscription

class SubscriptionListAdapter :
    ListAdapter<Subscription, SubscriptionListAdapter.SubscriptionListViewHolder>(
        SubscribeListDiffUtilCallBack()
    ) {

    class SubscriptionListViewHolder private constructor(private val binding: SubscribeListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(subscription: Subscription) {
            val item = subscription.lineItems[0]
            binding.item = item
            binding.subsStartDateTextView.text = subscription.startDate
            binding.subsEndDateTextView.text = subscription.endDate
            binding.subsStatusTextView.text = subscription.status
            binding.subscriptionsTotalTextView.text = "Amount Charged Daily: ${subscription.total} Rs/-"
            if (subscription.status == "active"){
                binding.subsStatusTextView.setTextColor(Color.parseColor("#009624"))
            }else{
                binding.subsStatusTextView.setTextColor(Color.parseColor("#FF0000"))
            }
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): SubscriptionListViewHolder {
                val binding = SubscribeListItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return SubscriptionListViewHolder(binding)
            }
        }
    }

    class SubscribeListDiffUtilCallBack : DiffUtil.ItemCallback<Subscription>() {
        override fun areItemsTheSame(oldItem: Subscription, newItem: Subscription): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Subscription, newItem: Subscription): Boolean {
            return oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubscriptionListViewHolder {
        return SubscriptionListViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: SubscriptionListViewHolder, position: Int) {
        val subscription = getItem(position)
        subscription?.let {
            holder.bind(it)
        }
    }
}