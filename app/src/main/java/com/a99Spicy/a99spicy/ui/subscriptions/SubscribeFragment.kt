package com.a99Spicy.a99spicy.ui.subscriptions

import android.content.DialogInterface
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
import com.a99Spicy.a99spicy.databinding.SubscribeFragmentBinding
import com.a99Spicy.a99spicy.domain.DomainProduct
import com.a99Spicy.a99spicy.network.*
import com.a99Spicy.a99spicy.ui.HomeActivity
import com.a99Spicy.a99spicy.ui.payment.PaymentActivity
import com.a99Spicy.a99spicy.ui.payment.PaymentMethodFragment
import com.a99Spicy.a99spicy.utils.AppUtils.Companion.getCurrentDate
import com.a99Spicy.a99spicy.utils.AppUtils.Companion.getDaysAfter
import com.a99Spicy.a99spicy.utils.AppUtils.Companion.getTransactionId
import com.a99Spicy.a99spicy.utils.Constants
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import timber.log.Timber

class SubscribeFragment : Fragment(), PaymentMethodFragment.OnPaymentMethodClickListener {

    private lateinit var viewModel: SubscribeViewModel
    private lateinit var subscribeBinding: SubscribeFragmentBinding
    private var qty = 1
    private lateinit var profile: Profile
    private lateinit var product: DomainProduct
    private lateinit var customerId: String
    private lateinit var shipping: Shipping
    private val lineItems: MutableList<SubLineItem> = mutableListOf()
    private var totalAmount = 0.0
    private lateinit var loadingDialog: AlertDialog
    private var salePrice: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        subscribeBinding = SubscribeFragmentBinding.inflate(inflater, container, false)

        //Initializing ViewModel class
        viewModel = ViewModelProvider(this).get(SubscribeViewModel::class.java)

        //Taking arguments
        val arguments = SubscribeFragmentArgs.fromBundle(requireArguments())
        product = arguments.product
        profile = arguments.profile
        shipping = profile.shipping
        subscribeBinding.product = product
        subscribeBinding.viewModel = viewModel
        subscribeBinding.lifecycleOwner = this

        val saleMeta = product.metaData[5]
        salePrice = saleMeta.value
        if (!salePrice.isNullOrEmpty()) {
            subscribeBinding.subscribePriceTextView.text = "${salePrice} Rs/-"
        } else {
            salePrice = product.salePrice
            if (!salePrice.isNullOrEmpty()) {
                subscribeBinding.subscribePriceTextView.text = "${product.salePrice} Rs/-"
            }
        }

        val activity = activity as HomeActivity
        customerId = activity.getUserId()

        //Observe qty LiveData value from ViewModel
        viewModel.qtyLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                qty = it
            }
        })

        //Set onClickListener to subscribe button
        subscribeBinding.productSubscribeButton.setOnClickListener {
            totalAmount = salePrice?.toDouble()!!.times(qty).times(7)
            val paymentFragment = PaymentMethodFragment(
                this,
                totalAmount.toString(),
                getString(R.string.subscribe)
            )
            paymentFragment.show(activity.supportFragmentManager, paymentFragment.tag)
        }

        //Observe subscribe LiveData
        viewModel.subscribeResponseLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                Toast.makeText(requireContext(), it.total.toString(), Toast.LENGTH_LONG).show()
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(it.status)
                    .setPositiveButton("Ok", DialogInterface.OnClickListener { dialog, _ ->
                        dialog.dismiss()
                    }).show()
            }
        })

        //Observe loading liveData
        viewModel.subsLoadingLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it == SubsLoading.SUCCESS) {
                    loadingDialog.dismiss()
                } else if (it == SubsLoading.FAILED) {
                    loadingDialog.dismiss()
                    Toast.makeText(
                        requireContext(),
                        "Failed to Subscribe Product. Contact support for Refund",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })

        //Observe wallet balance
        viewModel.walletBalanceLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it.toDouble() >= totalAmount) {
                    viewModel.cdWallet(
                        customerId,
                        WalletRequest(getString(R.string.debit), totalAmount, "subscribe")
                    )
                } else {
                    MaterialAlertDialogBuilder(requireContext())
                        .setMessage("Insufficient balance in your wallet!")
                        .setPositiveButton("Ok", DialogInterface.OnClickListener { dialog, _ ->
                            dialog.dismiss()
                        })
                        .show()
                }
            }
        })

        //observe wallet response liveData
        viewModel.walletResponseLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                Timber.e("Wallet Response: ${it.response}")
                if (it.response == getString(R.string.wallet_success)) {
                    subscribeProduct()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Failed to deduct amount from Wallet",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })
        return subscribeBinding.root
    }

    //Subscribe product
    private fun subscribeProduct() {
        lineItems.add(SubLineItem(product.id, qty))
        if (lineItems.isNotEmpty()) {
            val subscribeProduct = SubscribeProduct(
                customerId.toInt(), "active", "week", 1,
                getCurrentDate("YYYY-MM-dd hh:mm:ss"), getDaysAfter(7), "Wallet",
                true, getTransactionId(), shipping, lineItems
            )
            loadingDialog = createLoadingDialog()
            loadingDialog.show()
            viewModel.subscribeProduct(subscribeProduct)
        }
    }

    override fun onPaymentMethodClick(name: String) {
        Timber.e("total amount: $totalAmount")
        if (name == getString(R.string.credit_card_debit_card_upi) || name == getString(R.string.paytm)) {
            val intent = Intent(activity, PaymentActivity::class.java)
            intent.putExtra(Constants.AMOUNT, totalAmount.toString())
            intent.putExtra(Constants.TRANSACTION_MODE, name)
            intent.putExtra(Constants.TRANSACTION_TYPE, getString(R.string.subscribe_product))
            startActivityForResult(intent, Constants.SUBSCRIBE_PRODUCT_REQUEST_CODE)
        } else {
            viewModel.getWalletBalance(customerId)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Timber.e("Total amount: $totalAmount")
        if (requestCode == Constants.SUBSCRIBE_PRODUCT_REQUEST_CODE) {
            val message = data?.getStringExtra(Constants.MESSAGE)
            message?.let {
                if (it == getString(R.string.success)) {
                    subscribeProduct()
                } else {
                    Toast.makeText(requireContext(), "Payment Failed, Try Again", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    private fun createLoadingDialog(): AlertDialog {
        val layout = LayoutInflater.from(requireContext()).inflate(R.layout.loading_layout, null)
        val builder = AlertDialog.Builder(requireContext(), R.style.TransparentDialog)
        builder.setView(layout)
        builder.setCancelable(false)
        return builder.create()
    }
}