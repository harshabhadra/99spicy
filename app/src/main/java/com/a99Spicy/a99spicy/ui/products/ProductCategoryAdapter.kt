package com.a99Spicy.a99spicy.ui.products

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.a99Spicy.a99spicy.databinding.ProductListItemBinding
import com.a99Spicy.a99spicy.domain.DomainCategoryItem

class ProductCategoryAdapter(private val viewModelStoreOwner: ViewModelStoreOwner,private val viewLifecycleOwner: LifecycleOwner) :
    ListAdapter<DomainCategoryItem, ProductCategoryAdapter.ProductCategoryViewHolder>(
        ProductCategoryDiffUtilCallBack()
    ) {

    class ProductCategoryViewHolder private constructor(val binding: ProductListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(categoryItem: DomainCategoryItem, viewModelStoreOwner: ViewModelStoreOwner, viewLifecycleOwner: LifecycleOwner) {

            val productList = categoryItem.dummyProducts?.dummyProductList
            productList?.let {
                val productListAdapter = ProductListAdapter(viewModelStoreOwner,viewLifecycleOwner)
                binding.productListRecyclerView.adapter = productListAdapter
                productListAdapter.submitList(it)
            }
            binding.executePendingBindings()
        }

        companion object {

            fun from(parent: ViewGroup): ProductCategoryViewHolder {
                val binding = ProductListItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )

                return ProductCategoryViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductCategoryViewHolder {
        return ProductCategoryViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ProductCategoryViewHolder, position: Int) {

        val productCategory = getItem(position)
        productCategory?.let {
            holder.bind(it,viewModelStoreOwner, viewLifecycleOwner)
        }
    }
}

class ProductCategoryDiffUtilCallBack : DiffUtil.ItemCallback<DomainCategoryItem>() {
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