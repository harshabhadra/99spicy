package com.a99Spicy.a99spicy.ui.products

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.a99Spicy.a99spicy.R
import com.a99Spicy.a99spicy.database.DatabaseCart
import com.a99Spicy.a99spicy.databinding.ProductSubItemListBinding
import com.a99Spicy.a99spicy.domain.DomainProduct
import com.squareup.picasso.Picasso
import timber.log.Timber

private lateinit var viewModel: ProductListViewModel
private var qty = 0
private var pQty = 0

class ProductListAdapter(
    private val catName: String,
    private val owner: ViewModelStoreOwner,
    private val lifecycleOwner: LifecycleOwner,
    private val onProductItemClickListener: OnProductItemClickListener,
    private val onProductMinusClickListener: OnProductMinusClickListener
) :
    ListAdapter<DomainProduct, ProductListAdapter.ProductListViewHolder>(ProductListDiffUtilCallBack()) {

    interface OnProductItemClickListener {
        fun onProductItemClick(position: Int, quantity: Int)
    }

    interface OnProductMinusClickListener {
        fun onProductMinusClick(position: Int, quantity: Int)
    }

    class ProductListViewHolder private constructor(val binding: ProductSubItemListBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        fun bind(
            catName: String,
            domainDummyProduct: DomainProduct,
            viewLifeCycleOwner: LifecycleOwner,
            onProductItemClickListener: OnProductItemClickListener,
            onProductMinusClickListener: OnProductMinusClickListener
        ) {
            binding.product = domainDummyProduct
            val tv = binding.productDiscountTextView
            tv.paintFlags = tv.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

            val imgUrl = domainDummyProduct.images[0].src
            if (imgUrl.isNotEmpty()) {
                Picasso.get().load(imgUrl).placeholder(R.drawable.app_logo)
                    .error(R.drawable.grocery_place_holder).into(binding.productImageView)
            }

//            val pActualPrice = domainDummyProduct.salePrice?.toDouble()
//
//            pActualPrice?.let {
//                val save = domainDummyProduct.regularPrice?.toDouble()?.minus(it)
//                binding.productPriceTextView.text = "$pActualPrice Rs/-"
//                binding.savingTextView.text = "Your Save ${save} Rs/-"
//            }
            binding.addToCartButton.setOnClickListener {
                pQty = 0
                binding.addToCartButton.visibility = View.GONE
                binding.quantityLinearLayout.visibility = View.VISIBLE
            }

            //Increase quantity
            binding.addQuantityButton.setOnClickListener {
                pQty++
                viewModel.addItemToCart(
                    DatabaseCart(
                        domainDummyProduct.id,
                        domainDummyProduct.name,
                        domainDummyProduct.regularPrice,
                        domainDummyProduct.salePrice,
                        domainDummyProduct.images[0].src,
                        pQty
                    )
                )
                binding.productQtyTv.text = pQty.toString()
                onProductItemClickListener.onProductItemClick(adapterPosition, 1)
            }

            //Decrease quantity
            binding.minusQuantityButton.setOnClickListener {

                if (pQty > 1) {
                    pQty--
                    viewModel.addItemToCart(
                        DatabaseCart(
                            domainDummyProduct.id,
                            domainDummyProduct.name,
                            domainDummyProduct.regularPrice,
                            domainDummyProduct.salePrice,
                            domainDummyProduct.images[0].src,
                            pQty
                        )
                    )
                    binding.productQtyTv.text = pQty.toString()
                } else if (pQty == 1) {
                    pQty--
                    viewModel.removeItemFromCart(
                        DatabaseCart(
                            domainDummyProduct.id,
                            domainDummyProduct.name,
                            domainDummyProduct.regularPrice,
                            domainDummyProduct.salePrice,
                            domainDummyProduct.images[0].src,
                            pQty
                        )
                    )
                    binding.productQtyTv.text = pQty.toString()
                }
                onProductMinusClickListener.onProductMinusClick(adapterPosition, 1)
            }

            if (catName == "Dairy and Bakery"){
                binding.subscribeButton.visibility = View.VISIBLE
            }else{
                binding.subscribeButton.visibility = View.INVISIBLE
            }
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

        override fun onClick(v: View?) {
            binding.root.setOnClickListener(this)
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
            holder.bind(
                catName,
                it,
                lifecycleOwner,
                onProductItemClickListener,
                onProductMinusClickListener
            )
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
        return oldItem.id == newItem.id
    }

}