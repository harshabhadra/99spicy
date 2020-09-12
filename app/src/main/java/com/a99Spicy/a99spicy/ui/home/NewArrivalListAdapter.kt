package com.a99Spicy.a99spicy.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.a99Spicy.a99spicy.R
import com.a99Spicy.a99spicy.database.DatabaseCart
import com.a99Spicy.a99spicy.databinding.NewListItemBinding
import com.a99Spicy.a99spicy.domain.DomainProduct
import com.bumptech.glide.Glide

private lateinit var viewModel: HomeViewModel
private var pQty = 0

class NewArrivalListAdapter(
    private val owner: ViewModelStoreOwner,
    private val lifecycleOwner: LifecycleOwner
) :
    ListAdapter<DomainProduct, NewArrivalListAdapter.NewArrivalListViewHolder>(
        NewArrivalListDiffUtilCallBack()
    ) {

    class NewArrivalListViewHolder private constructor(private val binding: NewListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(domainProduct: DomainProduct) {
            binding.product = domainProduct
            val imgUrl = domainProduct.images[0].src
            imgUrl?.let {
                val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
                Glide.with(binding.newItemImageView.context)
                    .load(imgUri)
                    .placeholder(R.drawable.app_logo)
                    .error(R.drawable.app_logo)
                    .centerCrop()
                    .into(binding.newItemImageView)
            }
            binding.newAddToCartButton.setOnClickListener {
                pQty = 0
                binding.newAddToCartButton.visibility = View.GONE
                binding.newIemQuantityLinearLayout.visibility = View.VISIBLE
            }

            //Increase quantity
            binding.newAddQuantityButton.setOnClickListener {
                pQty++
                viewModel.addItemToCart(
                    DatabaseCart(
                        domainProduct.id,
                        domainProduct.name,
                        domainProduct.regularPrice,
                        domainProduct.salePrice,
                        domainProduct.images[0].src,
                        pQty
                    )
                )
                binding.newProductQtyTv.text = pQty.toString()
            }

            //Decrease quantity
            binding.newItemMinusQuantityButton.setOnClickListener {

                if (pQty > 1) {
                    pQty--
                    viewModel.addItemToCart(
                        DatabaseCart(
                            domainProduct.id,
                            domainProduct.name,
                            domainProduct.regularPrice,
                            domainProduct.salePrice,
                            domainProduct.images[0].src,
                            pQty
                        )
                    )
                    binding.newProductQtyTv.text = pQty.toString()
                } else if (pQty == 1) {
                    pQty--
                    viewModel.removeItemFromCart(
                        DatabaseCart(
                            domainProduct.id,
                            domainProduct.name,
                            domainProduct.regularPrice,
                            domainProduct.salePrice,
                            domainProduct.images[0].src,
                            pQty
                        )
                    )
                    binding.newProductQtyTv.text = pQty.toString()
                }
            }
            binding.executePendingBindings()
        }

        companion object {

            fun from(parent: ViewGroup): NewArrivalListViewHolder {
                val binding = NewListItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return NewArrivalListViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewArrivalListViewHolder {
        return NewArrivalListViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: NewArrivalListViewHolder, position: Int) {

        viewModel = ViewModelProvider(owner).get(HomeViewModel::class.java)
        val product = getItem(position)
        product?.let {
            holder.bind(it)
        }
    }
}

class NewArrivalListDiffUtilCallBack : DiffUtil.ItemCallback<DomainProduct>() {
    override fun areItemsTheSame(oldItem: DomainProduct, newItem: DomainProduct): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: DomainProduct, newItem: DomainProduct): Boolean {
        return oldItem.id == newItem.id
    }

}