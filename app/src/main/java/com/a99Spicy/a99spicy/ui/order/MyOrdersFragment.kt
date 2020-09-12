package com.a99Spicy.a99spicy.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.a99Spicy.a99spicy.R
import com.a99Spicy.a99spicy.databinding.FragmentMyOrdersBinding
import com.a99Spicy.a99spicy.ui.HomeActivity
import com.google.android.material.snackbar.Snackbar

class MyOrdersFragment : Fragment() {

    private lateinit var myOrdersListAdapter: MyOrdersListAdapter
    private lateinit var myOrdersBinding: FragmentMyOrdersBinding
    private lateinit var loadingDialog: AlertDialog
    private lateinit var viewModel: MyOrdersViewModel
    private lateinit var userId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        myOrdersBinding = FragmentMyOrdersBinding.inflate(inflater, container, false)

        //Initializing ViewModel class
        viewModel = ViewModelProvider(this).get(MyOrdersViewModel::class.java)

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
                myOrdersListAdapter.submitList(it)
                if (it.isEmpty()){
                    myOrdersBinding.noOrderLottie.visibility = View.VISIBLE
                    val snackBar = Snackbar.make(
                        myOrdersBinding.myOrdersLayout,
                        "You don't have any pending orders",
                        Snackbar.LENGTH_INDEFINITE
                    )
                    snackBar.animationMode = Snackbar.ANIMATION_MODE_FADE
                    snackBar.show()
                }else{
                    myOrdersBinding.noOrderLottie.visibility = View.GONE
                }
            }
        })

        //Observe loading value from ViewModel
        viewModel.loadingLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it == MyOrdersLoading.LOADING_SUCCESS){
                    loadingDialog.dismiss()
                }else if (it == MyOrdersLoading.LOADING_FAILED){
                    loadingDialog.dismiss()
                    Toast.makeText(requireContext(), "Failed to get Orders", Toast.LENGTH_SHORT).show()
                }
            }
        })
        return myOrdersBinding.root
    }

    private fun createLoadingDialog(): AlertDialog {
        val layout = LayoutInflater.from(requireContext()).inflate(R.layout.loading_layout, null)
        val builder = AlertDialog.Builder(requireContext(), R.style.TransparentDialog)
        builder.setView(layout)
        builder.setCancelable(false)
        return builder.create()
    }
}