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
import timber.log.Timber

private lateinit var viewModel: HomeViewModel
private var pQty = 0
private var salePrice: String? = null

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

            val index = domainProduct.index
            index?.let {
                if (index != -1) {
                    val saleMeta = domainProduct.metaData[index]
                    salePrice = saleMeta.value
                    Timber.e(("New arrival item sale price: $salePrice"))
                    binding.naSalePTv.text = "${salePrice} Rs/-"
                }
            }
            binding.newAddToCartButton.setOnClickListener {
                pQty = 1
                binding.newAddToCartButton.visibility = View.GONE
                binding.newIemQuantityLinearLayout.visibility = View.VISIBLE
                viewModel.addItemToCart(
                    DatabaseCart(
                        domainProduct.id,
                        domainProduct.name,
                        domainProduct.regularPrice,
                        domainProduct.metaData[index!!].value,
                        domainProduct.images[0].src,
                        pQty,
                        domainProduct.categories[0].id,
                        domainProduct.categories[1].id
                    )
                )
                binding.newProductQtyTv.text = pQty.toString()
            }

            //Increase quantity
            binding.newAddQuantityButton.setOnClickListener {
                pQty++
                viewModel.addItemToCart(
                    DatabaseCart(
                        domainProduct.id,
                        domainProduct.name,
                        domainProduct.regularPrice,
                        domainProduct.metaData[index!!].value,
                        domainProduct.images[0].src,
                        pQty,
                        domainProduct.categories[0].id,
                        domainProduct.categories[1].id
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
                            domainProduct.metaData[index!!].value,
                            domainProduct.images[0].src,
                            pQty,
                            domainProduct.categories[0].id,
                            domainProduct.categories[1].id
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
                            domainProduct.metaData[index!!].value,
                            domainProduct.images[0].src,
                            pQty,
                            domainProduct.categories[0].id,
                            domainProduct.categories[1].id
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