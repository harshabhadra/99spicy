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

private val productList: MutableSet<DomainProduct> = mutableSetOf()

class ProductCategoryAdapter(
    private val viewModelStoreOwner: ViewModelStoreOwner,
    private val viewLifecycleOwner: LifecycleOwner,
    private val onProductItemClickListener: ProductListAdapter.OnProductItemClickListener,
    private val onProductMinusClickListener: ProductListAdapter.OnProductMinusClickListener
) :
    ListAdapter<DomainProducts, ProductCategoryAdapter.ProductCategoryViewHolder>(
        ProductCategoryDiffUtilCallBack()
    ) {

    class ProductCategoryViewHolder private constructor(val binding: ProductListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            categoryItem: DomainProducts,
            viewModelStoreOwner: ViewModelStoreOwner,
            viewLifecycleOwner: LifecycleOwner,
            onProductItemClickListener: ProductListAdapter.OnProductItemClickListener,
            onProductMinusClickListener: ProductListAdapter.OnProductMinusClickListener
        ) {
            productList.addAll(categoryItem.productList)
            if (productList.isNotEmpty()) {
                val productListAdapter = ProductListAdapter(viewModelStoreOwner,
                    viewLifecycleOwner, onProductItemClickListener,onProductMinusClickListener)
                binding.productListRecyclerView.adapter = productListAdapter
                productListAdapter.submitList(categoryItem.productList.toList())
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
            holder.bind(it, viewModelStoreOwner, viewLifecycleOwner,
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