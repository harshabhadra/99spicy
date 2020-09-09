package com.a99Spicy.a99spicy.ui.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
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
    private val lifecycleOwner: LifecycleOwner
) :
    ListAdapter<DatabaseCart, CartListAdapter.CartListViewHolder>(CartListDiffUtilCallBack()) {

    class CartListViewHolder(private val binding: CartListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(databaseCart: DatabaseCart, clickListener: CartListItemClickListener) {
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
                        databaseCart.image, currentQty
                    )
                )
                binding.cartProductQtyTv.text = currentQty.toString()
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
                            databaseCart.image, currentQty
                        )
                    )
                } else {
                    if (currentQty == 1) {
                        currentQty--
                        viewModel.removeItemFromCart(databaseCart)
                    }
                }
                binding.cartProductQtyTv.text = currentQty.toString()
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
            holder.bind(it, clickListener)
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