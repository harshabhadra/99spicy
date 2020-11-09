package com.a99Spicy.a99spicy.ui.products

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.a99Spicy.a99spicy.databinding.ProductListItemBinding
import com.a99Spicy.a99spicy.domain.DomainProduct
import com.a99Spicy.a99spicy.domain.DomainProducts
import com.a99Spicy.a99spicy.network.Profile

private val productList: MutableSet<DomainProduct> = mutableSetOf()

class ProductCategoryAdapter(
    private val catName:String,
    private val profile: Profile,
    private val viewModelStoreOwner: ViewModelStoreOwner,
    private val viewModelFactory: ProductListViewModelFactory,
    private val onProductItemClickListener: ProductListAdapter.OnProductItemClickListener,
    private val onProductMinusClickListener: ProductListAdapter.OnProductMinusClickListener
) :
    ListAdapter<DomainProducts, ProductCategoryAdapter.ProductCategoryViewHolder>(
        ProductCategoryDiffUtilCallBack()
    ) {

    class ProductCategoryViewHolder private constructor(val binding: ProductListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            catName: String,
            profile: Profile,
            categoryItem: DomainProducts,
            viewModelStoreOwner: ViewModelStoreOwner,
            viewModelFactory: ProductListViewModelFactory,
            onProductItemClickListener: ProductListAdapter.OnProductItemClickListener,
            onProductMinusClickListener: ProductListAdapter.OnProductMinusClickListener
        ) {
            productList.addAll(categoryItem.productList)
            if (productList.isNotEmpty()) {
                val productListAdapter = ProductListAdapter(catName,profile,viewModelFactory, viewModelStoreOwner,
                     onProductItemClickListener,onProductMinusClickListener)
                binding.productListRecyclerView.adapter = productListAdapter
                binding.productListRecyclerView.recycledViewPool.setMaxRecycledViews(0,50)
                binding.productListRecyclerView.setItemViewCacheSize(50)
                productListAdapter.submitList(categoryItem.productList.toList())
                productListAdapter.notifyDataSetChanged()
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
            holder.bind(catName,profile, it, viewModelStoreOwner, viewModelFactory,
                onProductItemClickListener,onProductMinusClickListener)
        }
    }
}

class ProductCategoryDiffUtilCallBack : DiffUtil.ItemCallback<DomainProducts>() {
    override fun areItemsTheSame(
        oldItem: DomainProducts,
        newItem: DomainProducts
    ): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(
        oldItem: DomainProducts,
        newItem: DomainProducts
    ): Boolean {
        return oldItem.productList == newItem.productList
    }

}