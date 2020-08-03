package com.a99Spicy.a99spicy.ui.products

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.a99Spicy.a99spicy.databinding.ProductSubItemListBinding
import com.a99Spicy.a99spicy.domain.DomainProduct
import timber.log.Timber

private lateinit var viewModel: ProductListViewModel
private var qty = 0
class ProductListAdapter(
    private val owner: ViewModelStoreOwner,
    private val lifecycleOwner: LifecycleOwner
) :
    ListAdapter<DomainProduct, ProductListAdapter.ProductListViewHolder>(ProductListDiffUtilCallBack()) {

    class ProductListViewHolder private constructor(val binding: ProductSubItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(domainProduct: DomainProduct, viewLifeCycleOwner: LifecycleOwner) {
            binding.product = domainProduct
            val tv = binding.productDiscountTextView
            tv.paintFlags = tv.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

            val pActualPrice = domainProduct.productPrice.toDouble()
            val discount = domainProduct.productDiscount.toDouble()
            val productPrice = (discount / 100) * pActualPrice
            val save = pActualPrice.minus(productPrice)
            binding.productPriceTextView.text = "$productPrice Rs/-"
            binding.savingTextView.text = "Your Save ${save} Rs/-"

            binding.addToCartButton.setOnClickListener {
                binding.addToCartButton.visibility = View.GONE
                binding.quantityLinearLayout.visibility = View.VISIBLE
            }

            binding.minusQuantityButton.setOnClickListener {

                binding.addToCartButton.visibility = View.VISIBLE
                binding.quantityLinearLayout.visibility = View.GONE
            }

            //Increase quantity
            binding.addQuantityButton.setOnClickListener {
                viewModel.addQuantity()
            }

            //Decrease quantity
            binding.minusQuantityButton.setOnClickListener {
                if (qty>1) {
                    viewModel.minusQuantity()
                }else{
                    viewModel.minusQuantity()
                    binding.quantityLinearLayout.visibility = View.GONE
                    binding.addToCartButton.visibility = View.VISIBLE
                }
            }

            viewModel.productQtyLiveData.observe(viewLifeCycleOwner, Observer {
                it?.let {
                    qty = it
                    Timber.e("Product quantity: $qty")
                    binding.productQtyTv.text = it.toString()
                }
            })

            binding.executePendingBindings()
        }

        companion object {

            fun from(parent: ViewGroup): ProductListViewHolder {
                val binding = ProductSubItemListBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )

                return ProductListViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductListViewHolder {
        Timber.e("Product list adapter")
        return ProductListViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ProductListViewHolder, position: Int) {

        viewModel = ViewModelProvider(owner).get(ProductListViewModel::class.java)
        val product = getItem(position)
        product?.let {
            holder.bind(it, lifecycleOwner)
            Timber.e("Product name: ${it.productName}")
        } ?: let {
            Timber.e("Product is empty")
        }
    }
}

class ProductListDiffUtilCallBack : DiffUtil.ItemCallback<DomainProduct>() {
    override fun areItemsTheSame(oldItem: DomainProduct, newItem: DomainProduct): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: DomainProduct, newItem: DomainProduct): Boolean {
        return oldItem.productId == newItem.productId
    }

}