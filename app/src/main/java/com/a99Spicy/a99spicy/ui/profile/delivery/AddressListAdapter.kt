package com.a99Spicy.a99spicy.ui.profile.delivery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.a99Spicy.a99spicy.databinding.AddressListItemBinding
import com.a99Spicy.a99spicy.domain.DeliveryAddress

class AddressListAdapter(private val clickListener: AddressItemClickListener) : ListAdapter<DeliveryAddress, AddressListAdapter.AddressListViewHolder>(
    AddressItemDiffUtilCallback()
) {

    class AddressListViewHolder(private val binding: AddressListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(deliveryAddress: DeliveryAddress, clickListener: AddressItemClickListener, position: Int) {
            binding.address = deliveryAddress
            if (position == 0){
                binding.defaultAddressTextView.visibility = View.VISIBLE
                binding.setAsDefaultTextView.visibility = View.INVISIBLE
            }else{
                binding.clickListener = clickListener
                binding.defaultAddressTextView.visibility = View.GONE
                binding.setAsDefaultTextView.visibility = View.VISIBLE
            }
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): AddressListViewHolder {
                val binding = AddressListItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent, false
                )
                return AddressListViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressListViewHolder {
        return AddressListViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: AddressListViewHolder, position: Int) {

        val address = getItem(position)
        address?.let {
            holder.bind(address, clickListener, position)
        }
    }
}

class AddressItemClickListener(val clickListener: (address: DeliveryAddress) -> Unit) {
    fun onAddressItemClick(address: DeliveryAddress) = clickListener(address)
}

class AddressItemDiffUtilCallback : DiffUtil.ItemCallback<DeliveryAddress>() {
    override fun areItemsTheSame(oldItem: DeliveryAddress, newItem: DeliveryAddress): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: DeliveryAddress, newItem: DeliveryAddress): Boolean {
        return oldItem.id == newItem.id
    }

}