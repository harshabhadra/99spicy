package com.a99Spicy.a99spicy.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.a99Spicy.a99spicy.R
import com.a99Spicy.a99spicy.databinding.FragmentWalletBinding
import com.a99Spicy.a99spicy.network.WalletRequest
import com.a99Spicy.a99spicy.ui.HomeActivity
import com.a99Spicy.a99spicy.ui.payment.PaymentActivity
import com.a99Spicy.a99spicy.utils.Constants
import kotlinx.android.synthetic.main.add_money_layout.view.*
import timber.log.Timber

class WalletFragment : Fragment() {

    private lateinit var walletViewModel: WalletViewModel
    private lateinit var walletBinding: FragmentWalletBinding
    private lateinit var walletTransactionAdapter: WalletTransactionsAdapter

    private lateinit var loadingDialog: AlertDialog
    private lateinit var userId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        walletBinding = FragmentWalletBinding.inflate(inflater, container, false)

        //Initializing ViewModel class
        walletViewModel = ViewModelProvider(this).get(WalletViewModel::class.java)

        val activity = activity as HomeActivity
        activity.setToolbarTitle(getString(R.string.wallet))
        activity.setAppBarElevation(0f)
        activity.setToolbarLogo(null)
        userId = activity.getUserId()

        loadingDialog = createLoadingDialog()
        loadingDialog.show()

        //Getting wallet balance
        walletViewModel.getWalletBalance(userId)

        //Getting wallet transactions
        walletViewModel.getWalletTransactions(userId)

        //Observe wallet balance
        walletViewModel.walletBalanceLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                walletBinding.walletBalanceTextView.text = "$it Rs/-"
            }
        })

        //Setting up transaction recyclerView
        walletTransactionAdapter = WalletTransactionsAdapter(requireContext())
        walletBinding.transactionListRecyclerView.adapter = walletTransactionAdapter
        //observe wallet transactions
        walletViewModel.walletTransactionLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                Toast.makeText(
                    requireContext(),
                    "No. of Transactions: ${it.size}",
                    Toast.LENGTH_LONG
                ).show()
                walletTransactionAdapter.submitList(it)
            }
        })

        //Observe loading status
        walletViewModel.loadingLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it == WalletLoading.SUCCESS || it == WalletLoading.FAILED) {
                    loadingDialog.dismiss()
                } else {
                    loadingDialog.show()
                }
            }
        })

        //Observe wallet livedata
        walletViewModel.walletResponseLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                Toast.makeText(requireContext(), it.response, Toast.LENGTH_SHORT).show()
                walletViewModel.getWalletBalance(userId)
                walletViewModel.getWalletTransactions(userId)
            }
        })

        //Set onClickListener to add money button
        walletBinding.addMoneyButton.setOnClickListener {
            createAmountDialog()
        }
        return walletBinding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.ADD_TO_WALLET_REQUEST_CODE) {
            val message = data?.getStringExtra(Constants.MESSAGE)
            message?.let {
                if (it == getString(R.string.success)) {
                    val amount = data.getDoubleExtra(Constants.AMOUNT, 0.0).div(100)
                    val walletRequest = WalletRequest("credit", amount, "Add to Wallet")
                    walletViewModel.cdWallet(userId, walletRequest)
                    Timber.e("Adding to wallet")
                    loadingDialog = createLoadingDialog()
                    loadingDialog.show()
                }
            }
        }
    }

    private fun createAmountDialog() {
        val layout = LayoutInflater.from(requireContext()).inflate(R.layout.add_money_layout, null)
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(layout)

        val amountTextInput = layout.wallet_money_textInput
        val addButton = layout.add_m_w_button

        val dialog = builder.create()
        dialog.show()

        addButton.setOnClickListener {
            val amount = amountTextInput.text.toString()
            when {
                amount.isEmpty() -> {
                    Toast.makeText(requireContext(), "Enter Amount", Toast.LENGTH_SHORT).show()
                }
                amount.toInt() < 1 -> {
                    Toast.makeText(
                        requireContext(),
                        "Cannot add amount less than 10 Rs/-",
                        Toast.LENGTH_LONG
                    ).show()
                }
                else -> {
                    dialog.dismiss()
                    val intent = Intent(activity, PaymentActivity::class.java)
                    intent.putExtra(Constants.AMOUNT, amount.toString())
                    intent.putExtra(
                        Constants.TRANSACTION_MODE,
                        getString(R.string.credit_card_debit_card_upi)
                    )
                    intent.putExtra(Constants.TRANSACTION_TYPE, getString(R.string.add_to_wallet))
                    startActivityForResult(intent, Constants.ADD_TO_WALLET_REQUEST_CODE)
                }
            }
        }
    }

    private fun createLoadingDialog(): AlertDialog {
        val layout = LayoutInflater.from(requireContext()).inflate(R.layout.loading_layout, null)
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(layout)
        builder.setCancelable(false)
        return builder.create()
    }
}