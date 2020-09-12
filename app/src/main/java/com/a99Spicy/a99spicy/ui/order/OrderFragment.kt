package com.a99Spicy.a99spicy.ui.order

import android.app.NotificationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.a99Spicy.a99spicy.R
import com.a99Spicy.a99spicy.network.OrderResponse
import com.a99Spicy.a99spicy.network.PlaceOrder
import com.a99Spicy.a99spicy.utils.createNotification
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class OrderFragment(
    private val order: PlaceOrder,
    private val onOrderCompleteListener: OnOrderCompleteListener
) : BottomSheetDialogFragment() {
    private lateinit var viewModel: OrderViewModel

    interface OnOrderCompleteListener {
        fun onOrderComplete(orderResponse: OrderResponse)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.order_fragment, container, false)
        viewModel = ViewModelProvider(this).get(OrderViewModel::class.java)

        val notificationManager = ContextCompat.getSystemService(
            requireContext(),
            NotificationManager::class.java
        ) as NotificationManager

        //Place order
        viewModel.placeOrder(order)
        viewModel.orderLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                Toast.makeText(requireContext(), "Order response: ${it.status}", Toast.LENGTH_SHORT)
                    .show()
                onOrderCompleteListener.onOrderComplete(it)

                val orderDetails =
                    "Orders Details\nDate: ${it.dateCreated}\nTotal items: ${it.lineItems.size}\nOrder Total: ${it.total}\n\nWe've received your order and will deliver to your door-step as scheduled"
                notificationManager.createNotification(
                    "Order Placed Successfully",
                    orderDetails,
                    requireContext()
                )
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
}