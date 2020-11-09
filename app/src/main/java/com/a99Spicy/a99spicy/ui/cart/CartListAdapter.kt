 package com.a99Spicy.a99spicy.ui.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.a99Spicy.a99spicy.database.DatabaseCart
import com.a99Spicy.a99spicy.databinding.CartListItemBinding

private lateinit var viewModel: CartViewModel

class CartListAdapter(
    private val clickListener: CartListItemClickListener,
    private val owner: ViewModelStoreOwner,
    private val onCartListClickListener: CartListAdapter.OnCartListClickListener
) :
    ListAdapter<DatabaseCart, CartListAdapter.CartListViewHolder>(CartListDiffUtilCallBack()) {

    interface OnCartListClickListener {
        fun onCartListItemClick(cart: DatabaseCart)
    }

    class CartListViewHolder(private val binding: CartListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            databaseCart: DatabaseCart,
            clickListener: CartListItemClickListener,
            onCartListClickListener: OnCartListClickListener
        ) {
            binding.product = databaseCart
            binding.clickListener = clickListener
            var currentQty = databaseCart.quantity

            //Set onClickListener to add qty button
            binding.cartAddQuantityButton.setOnClickListener {
                currentQty++
                viewModel.updateCartItem(
                    DatabaseCart(
                        databaseCart.productId,
                        databaseCart.name,
                        databaseCart.regularPrice,
                        databaseCart.salePrice,
                        databaseCart.image,
                        currentQty,
                        databaseCart.catId,
                        databaseCart.subCatId
                    )
                )
                binding.cartProductQtyTv.text = currentQty.toString()
                onCartListClickListener.onCartListItemClick(databaseCart)
            }

            //Set onClickListener to minus qty button
            binding.cartMinusQuantityButton.setOnClickListener {
                if (currentQty > 1) {
                    currentQty--
                    viewModel.updateCartItem(
                        DatabaseCart(
                            databaseCart.productId,
                            databaseCart.name,
                            databaseCart.regularPrice,
                            databaseCart.salePrice,
                            databaseCart.image,
                            currentQty,
                            databaseCart.catId,
                            databaseCart.subCatId
                        )
                    )
                } else {
                    if (currentQty == 1) {
                        currentQty--
                        viewModel.removeItemFromCart(databaseCart)
                    }
                }
                binding.cartProductQtyTv.text = currentQty.toString()
                onCartListClickListener.onCartListItemClick(databaseCart)
            }
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): CartListViewHolder {

                val binding = CartListItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return CartListViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartListViewHolder {
        return CartListViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: CartListViewHolder, position: Int) {
        viewModel = ViewModelProvider(owner).get(CartViewModel::class.java)
        val product = getItem(position)
        product?.let {
            holder.bind(it, clickListener, onCartListClickListener)
        }
    }
}

class CartListItemClickListener(val clickListener: (databaseCart: DatabaseCart) -> Unit) {
    fun onCartListItemClick(databaseCart: DatabaseCart) = clickListener(databaseCart)
}

class CartListDiffUtilCallBack : DiffUtil.ItemCallback<DatabaseCart>() {
    override fun areItemsTheSame(oldItem: DatabaseCart, newItem: DatabaseCart): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: DatabaseCart, newItem: DatabaseCart): Boolean {
        return oldItem.productId == newItem.productId
    }

}