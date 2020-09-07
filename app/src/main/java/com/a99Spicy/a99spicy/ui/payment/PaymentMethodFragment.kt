package com.a99Spicy.a99spicy.ui.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.a99Spicy.a99spicy.R
import com.a99Spicy.a99spicy.databinding.FragmentPaymentMethodBinding
import com.a99Spicy.a99spicy.ui.cart.CartFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PaymentMethodFragment(private val onPaymentMethodClickListener: OnPaymentMethodClickListener) :
    BottomSheetDialogFragment() {

    private lateinit var paymentMethodBinding: FragmentPaymentMethodBinding
    private var isRazorPay = true
    private var isPayTM = false

    interface OnPaymentMethodClickListener {
        fun onPaymentMethodClick(name: String)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        paymentMethodBinding = FragmentPaymentMethodBinding.inflate(inflater, container, false)

        //Add Check Change listener to radio group
        paymentMethodBinding.paymentMethodRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.razor_pay -> {
                    isRazorPay = true
                    isPayTM = false
                }
                R.id.paytm -> {
                    isRazorPay = false
                    isPayTM = true
                }
                else -> {
                    isRazorPay = false
                    isPayTM = false
                }
            }
        }

        //Set onClickListener to proceed to pay button
        paymentMethodBinding.proceedToPayButton.setOnClickListener {
            when {
                isRazorPay -> {
                    onPaymentMethodClickListener.onPaymentMethodClick(getString(R.string.credit_card_debit_card_upi))
                }
                isPayTM -> {
                    onPaymentMethodClickListener.onPaymentMethodClick(getString(R.string.paytm))
                }
                else -> {
                    onPaymentMethodClickListener.onPaymentMethodClick(getString(R.string.wallet))
                }
            }
            dismiss()
        }
        return paymentMethodBinding.root
    }
}