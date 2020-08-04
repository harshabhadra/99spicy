package com.a99Spicy.a99spicy.ui.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.a99Spicy.a99spicy.databinding.ProfileListItemBinding

private var nameList:MutableList<String> = mutableListOf()

class ProfileItemsAdapter(
    private val clickListener: ProfileItemClickListener
) :
    RecyclerView.Adapter<ProfileItemsAdapter.ProfileItemViewHolder>() {

    class ProfileItemViewHolder(private val binding: ProfileListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(name: String, clickListener: ProfileItemClickListener) {
            binding.name = name
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ProfileItemViewHolder {

                val binding = ProfileListItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return ProfileItemViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileItemViewHolder {
        return ProfileItemViewHolder.from(parent)
    }

    override fun getItemCount(): Int {
        return nameList.size
    }

    fun setProfileNameList( profileList:MutableList<String>){
        nameList.clear()
        nameList.addAll(profileList)
        notifyDataSetChanged()
    }
    override fun onBindViewHolder(holder: ProfileItemViewHolder, position: Int) {
        val name = nameList[position]
        holder.bind(name, clickListener)
    }
}

class ProfileItemClickListener(val clickListener: (name: String) -> Unit) {
    fun onProfileItemClick(name: String) = clickListener(name)
}