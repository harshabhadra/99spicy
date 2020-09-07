package com.a99Spicy.a99spicy.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.a99Spicy.a99spicy.R
import com.a99Spicy.a99spicy.network.OrderResponse
import com.a99Spicy.a99spicy.network.PlaceOrder
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class OrderFragment(
    private val order: PlaceOrder,
    private val onOrderCompleteListener: OnOrderCompleteListener
) : BottomSheetDialogFragment() {


    private lateinit var viewModel: OrderViewModel
    private lateinit var loadingDialog: AlertDialog

    interface OnOrderCompleteListener {
        fun onOrderComplete(orderResponse: OrderResponse)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.order_fragment, container, false)
        viewModel = ViewModelProvider(this).get(OrderViewModel::class.java)

        viewModel.placeOrder(order)
        viewModel.orderLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                Toast.makeText(requireContext(), "Order response: ${it.status}", Toast.LENGTH_SHORT)
                    .show()
                onOrderCompleteListener.onOrderComplete(it)
            }
        })

        viewModel.loadingLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it == Loading.SUCCESS || it == Loading.FAILED) {
                    dismiss()
                }
            }
        })
        return view
    }

    private fun createLoadingDialog(): AlertDialog {
        val layout = LayoutInflater.from(requireContext()).inflate(R.layout.loading_layout, null)
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(layout)
        builder.setCancelable(false)
        return builder.create()
    }
}