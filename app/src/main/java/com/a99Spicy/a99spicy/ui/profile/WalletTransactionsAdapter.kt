package com.a99Spicy.a99spicy.ui.profile

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.a99Spicy.a99spicy.R
import com.a99Spicy.a99spicy.databinding.TransactionsListItemBinding
import com.a99Spicy.a99spicy.network.WalletTransaction
import java.util.*

class WalletTransactionsAdapter(val context: Context) :
    ListAdapter<WalletTransaction, WalletTransactionsAdapter.WalletTransactionViewHolder>
        (WalletTransactionDiffUtilCallBack()) {

    class WalletTransactionViewHolder private constructor(val binding: TransactionsListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(walletTransaction: WalletTransaction, context: Context) {
            binding.walletTransaction = walletTransaction
            if (walletTransaction.type.toUpperCase(Locale.ROOT) == "CREDIT") {
                binding.textView12.setTextColor(ContextCompat.getColor(context, R.color.colorGreen))
            }else{
                binding.textView12.setTextColor(Color.RED)
            }
            binding.executePendingBindings()
        }

        companion object {

            fun from(parent: ViewGroup): WalletTransactionViewHolder {

                val binding = TransactionsListItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return WalletTransactionViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalletTransactionViewHolder {
        return WalletTransactionViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: WalletTransactionViewHolder, position: Int) {

        val walletTransaction = getItem(position)
        walletTransaction?.let {
            holder.bind(it,context)
        }
    }
}

class WalletTransactionDiffUtilCallBack : DiffUtil.ItemCallback<WalletTransaction>() {
    override fun areItemsTheSame(oldItem: WalletTransaction, newItem: WalletTransaction): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(
        oldItem: WalletTransaction,
        newItem: WalletTransaction
    ): Boolean {
        return oldItem.transactionId == newItem.transactionId
    }

}