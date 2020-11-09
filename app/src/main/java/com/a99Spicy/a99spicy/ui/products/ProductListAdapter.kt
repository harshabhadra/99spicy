package com.a99Spicy.a99spicy.ui.products

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.a99Spicy.a99spicy.R
import com.a99Spicy.a99spicy.database.DatabaseCart
import com.a99Spicy.a99spicy.databinding.ProductSubItemListBinding
import com.a99Spicy.a99spicy.domain.DomainProduct
import com.a99Spicy.a99spicy.network.Profile
import com.squareup.picasso.Picasso
import timber.log.Timber
import java.util.*
import kotlin.math.roundToInt

private lateinit var viewModel: ProductListViewModel
private var pQty = 0
private var regularPrice: String? = null
private var salePrice: String? = null
private var saving: String? = null
private var savingPercent: String? = null

class ProductListAdapter(
    private val catName: String?,
    private val profile: Profile?,
    private val viewModelFactory: ProductListViewModelFactory,
    private val owner: ViewModelStoreOwner,
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
            catName: String?,
            domainDummyProduct: DomainProduct,
            onProductItemClickListener: OnProductItemClickListener,
            onProductMinusClickListener: OnProductMinusClickListener,
            profile: Profile?,
        ) {
            binding.product = domainDummyProduct

            binding.productRegularPriceTextView.paintFlags =
                binding.productRegularPriceTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

            val imgUrl = domainDummyProduct.images[0].src
            if (imgUrl.isNotEmpty()) {
                Picasso.get().load(imgUrl).placeholder(R.drawable.app_logo)
                    .error(R.drawable.grocery_place_holder).into(binding.productImageView)
            }

            val index = domainDummyProduct.index
            val metDataList = domainDummyProduct.metaData
            index?.let {
                if (index != -1) {
                    val regularMeta = metDataList[index - 1]
                    regularPrice = regularMeta.value
                    if (regularPrice.isNullOrEmpty()) {
                        regularPrice = domainDummyProduct.regularPrice
                    }
                    val saleMeta = metDataList[index]
                    salePrice = saleMeta.value
                    if (salePrice.isNullOrEmpty()) {
                        salePrice = domainDummyProduct.salePrice
                    }
                } else {
                    regularPrice = domainDummyProduct.regularPrice
                    salePrice = domainDummyProduct.salePrice
                }
            }
            if (!regularPrice.isNullOrEmpty() && !salePrice.isNullOrEmpty()) {

                binding.productRegularPriceTextView.text = "${regularPrice} Rs/-"
                binding.productPriceTextView.text = "${salePrice} Rs/-"
                saving = regularPrice?.toDouble()?.minus(salePrice!!.toDouble()).toString()

                if (saving != "0.0") {
                    binding.savingTextView.visibility = View.VISIBLE
                    binding.discountFrameLayout.visibility = View.VISIBLE
                    binding.productRegularPriceTextView.visibility = View.VISIBLE
                    binding.savingTextView.text = "Your save ${saving.toString()} Rs/-"
                    val sP = (saving?.toDouble()?.div(regularPrice?.toDouble()!!)?.times(100)
                        ?.roundToInt())
                    savingPercent = sP.toString()
                    binding.discountPercentTextView.text = "${savingPercent}%\nOFF"
                } else {
                    binding.savingTextView.visibility = View.INVISIBLE
                    binding.discountFrameLayout.visibility = View.INVISIBLE
                    binding.productRegularPriceTextView.visibility = View.INVISIBLE
                }
            }

            //Set onClickListener to add to cart button
            binding.addToCartButton.setOnClickListener {
                Timber.e("Item position: $adapterPosition")
                Timber.e("sale price: $salePrice")
                pQty = 1
                binding.addToCartButton.visibility = View.GONE
                binding.quantityLinearLayout.visibility = View.VISIBLE
                viewModel.addItemToCart(
                    DatabaseCart(
                        domainDummyProduct.id,
                        domainDummyProduct.name,
                        regularPrice,
                        domainDummyProduct.metaData[index!!].value,
                        domainDummyProduct.images[0].src,
                        pQty,
                        domainDummyProduct.categories[0].id,
                        domainDummyProduct.categories[1].id
                    )
                )
            }

            //Increase quantity
            binding.addQuantityButton.setOnClickListener {
                Timber.e("Item position: $adapterPosition")
                pQty++
                viewModel.addItemToCart(
                    DatabaseCart(
                        domainDummyProduct.id,
                        domainDummyProduct.name,
                        regularPrice,
                        domainDummyProduct.metaData[index!!].value,
                        domainDummyProduct.images[0].src,
                        pQty,
                        domainDummyProduct.categories[0].id,
                        domainDummyProduct.categories[1].id
                    )
                )
                binding.productQtyTv.text = pQty.toString()
                onProductItemClickListener.onProductItemClick(adapterPosition, 1)
            }

            //Decrease quantity
            binding.minusQuantityButton.setOnClickListener {
                Timber.e("Item position: $adapterPosition")
                if (pQty > 1) {
                    pQty--
                    viewModel.addItemToCart(
                        DatabaseCart(
                            domainDummyProduct.id,
                            domainDummyProduct.name,
                            regularPrice,
                            domainDummyProduct.metaData[index!!].value,
                            domainDummyProduct.images[0].src,
                            pQty,
                            domainDummyProduct.categories[0].id,
                            domainDummyProduct.categories[1].id
                        )
                    )
                    binding.productQtyTv.text = pQty.toString()
                } else if (pQty == 1) {
                    pQty--
                    viewModel.removeItemFromCart(
                        DatabaseCart(
                            domainDummyProduct.id,
                            domainDummyProduct.name,
                            regularPrice,
                            domainDummyProduct.metaData[index!!].value,
                            domainDummyProduct.images[0].src,
                            pQty,
                            domainDummyProduct.categories[0].id,
                            domainDummyProduct.categories[1].id
                        )
                    )
                    binding.productQtyTv.text = pQty.toString()
                }
                onProductMinusClickListener.onProductMinusClick(adapterPosition, 1)
            }

            catName?.let {
                Timber.e("Category name: $it")
                if (it.toLowerCase(Locale.getDefault()) == "milk") {
                    binding.subscribeButton.visibility = View.VISIBLE
                } else {
                    binding.subscribeButton.visibility = View.INVISIBLE
                }
            } ?: Timber.e("Category name is null")

            //Set onClickListener to subscribe button
            binding.subscribeButton.setOnClickListener {
                it.findNavController().navigate(
                    ProductListFragmentDirections.actionProductListFragmentToSubscribeFragment(
                        domainDummyProduct, profile!!
                    )
                )
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
        return ProductListViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ProductListViewHolder, position: Int) {

        viewModel = ViewModelProvider(owner, viewModelFactory).get(ProductListViewModel::class.java)
        val product = getItem(position)
        product?.let {
            holder.bind(
                catName,
                it,
                onProductItemClickListener,
                onProductMinusClickListener,
                profile
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