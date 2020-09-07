package com.a99Spicy.a99spicy.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.a99Spicy.a99spicy.databinding.FragmentOrderDetailsBinding
import com.a99Spicy.a99spicy.network.LineItem
import com.a99Spicy.a99spicy.network.ResponseLineItem

class OrderDetailsFragment : Fragment() {

    private lateinit var orderDetailsFragmentBinding: FragmentOrderDetailsBinding
    private lateinit var orderDetailsListAdapter: OrderDetailsListAdapter
    private lateinit var lineItemsList:List<ResponseLineItem>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        orderDetailsFragmentBinding =
            FragmentOrderDetailsBinding.inflate(inflater, container, false)

        val arguments = OrderDetailsFragmentArgs.fromBundle(requireArguments())
        orderDetailsFragmentBinding.order = arguments.order
        lineItemsList = arguments.order.lineItems

        orderDetailsListAdapter = OrderDetailsListAdapter()
        orderDetailsFragmentBinding.orderDetailsRecyclerView.adapter = orderDetailsListAdapter
        orderDetailsListAdapter.submitList(lineItemsList)

        return orderDetailsFragmentBinding.root
    }

}