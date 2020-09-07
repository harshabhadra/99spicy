package com.a99Spicy.a99spicy.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.a99Spicy.a99spicy.R
import com.a99Spicy.a99spicy.databinding.FragmentMyOrdersBinding
import com.a99Spicy.a99spicy.ui.HomeActivity

class MyOrdersFragment : Fragment() {

    private lateinit var myOrdersListAdapter: MyOrdersListAdapter
    private lateinit var myOrdersBinding: FragmentMyOrdersBinding
    private lateinit var loadingDialog: AlertDialog
    private lateinit var viewModel: OrderViewModel
    private lateinit var userId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        myOrdersBinding = FragmentMyOrdersBinding.inflate(inflater, container, false)

        //Initializing ViewModel class
        viewModel = ViewModelProvider(this).get(OrderViewModel::class.java)

        val activity = activity as HomeActivity
        activity.setAppBarElevation(10F)
        activity.setToolbarTitle(getString(R.string.orders))
        activity.setToolbarLogo(null)
        userId = activity.getUserId()

        loadingDialog = createLoadingDialog()
        loadingDialog.show()

        //Getting all orders
        viewModel.getAllOrders(userId.toInt())

        //Setting up the recyclerView
        myOrdersListAdapter = MyOrdersListAdapter(MyOrderListItemClickListener {
            findNavController().navigate(
                MyOrdersFragmentDirections.actionMyOrdersFragmentToOrderDetailsFragment(
                    it
                )
            )
        })
        myOrdersBinding.myOrdersRecyclerView.adapter = myOrdersListAdapter

        //Observing orders liveData
        viewModel.allOrdersLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                loadingDialog.dismiss()
                myOrdersListAdapter.submitList(it)
            }
        })

        return myOrdersBinding.root
    }

    private fun createLoadingDialog(): AlertDialog {
        val layout = LayoutInflater.from(requireContext()).inflate(R.layout.loading_layout, null)
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(layout)
        builder.setCancelable(false)
        return builder.create()
    }
}