package com.a99Spicy.a99spicy.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.a99Spicy.a99spicy.databinding.CategoryListItemBinding
import com.a99Spicy.a99spicy.domain.DomainCategoryItem

class HomeCategoryAdapter(val clickListener: HomeCategoryClickListener):
ListAdapter<DomainCategoryItem,HomeCategoryAdapter.HomeCategoryViewHolder>(HomeCategoryDiffUtilCallBack()){

    class HomeCategoryViewHolder private constructor(val binding: CategoryListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(domainCategoryItem: DomainCategoryItem, clickListener: HomeCategoryClickListener) {
            binding.categoryItem = domainCategoryItem
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {

            fun from(parent: ViewGroup): HomeCategoryViewHolder {

                val binding = CategoryListItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent, false
                )

                return HomeCategoryViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeCategoryViewHolder {
        return HomeCategoryViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: HomeCategoryViewHolder, position: Int) {

        val category = getItem(position)
        category?.let {
            holder.bind(category,clickListener)
        }
    }
}

class HomeCategoryClickListener(val clickListener:(category:DomainCategoryItem)->Unit){
    fun onHomeCategoryClick(category: DomainCategoryItem) = clickListener(category)
}

class HomeCategoryDiffUtilCallBack:DiffUtil.ItemCallback<DomainCategoryItem>(){
    override fun areItemsTheSame(
        oldItem: DomainCategoryItem,
        newItem: DomainCategoryItem
    ): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(
        oldItem: DomainCategoryItem,
        newItem: DomainCategoryItem
    ): Boolean {
        return oldItem.catName == newItem.catName
    }

}