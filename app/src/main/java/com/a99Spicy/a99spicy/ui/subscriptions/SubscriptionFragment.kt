package com.a99Spicy.a99spicy.ui.subscriptions

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
import com.a99Spicy.a99spicy.ui.HomeActivity
import kotlinx.android.synthetic.main.fragment_dashboard.view.*
import timber.log.Timber


class SubscriptionFragment : Fragment() {

    private lateinit var subscriptionViewModel: SubscriptionViewModel
    private lateinit var subscriptionListAdapter: SubscriptionListAdapter
    private lateinit var loadingDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        subscriptionViewModel =
            ViewModelProvider(this).get(SubscriptionViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)

        val activity = activity as HomeActivity
        activity.setAppBarElevation(10F)
        activity.setToolbarTitle(getString(R.string.subscriptions))
        activity.setToolbarLogo(null)
        val customer = activity.getUserId()
        Timber.e("Customer id: $customer")
        //Getting subscriptions
        customer?.let {
            subscriptionViewModel.getAllSubscriptions(customer.toString())
            loadingDialog = createLoadingDialog()
            loadingDialog.show()
        }

        val lottieView = root.subs_empty_view
        //Setting up Recyclerview
        val recyclerView = root.subs_list_recyclerView
        subscriptionListAdapter = SubscriptionListAdapter()
        recyclerView.adapter = subscriptionListAdapter


        //Observe subscription list
        subscriptionViewModel.subscriptionsLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                Toast.makeText(requireContext(), "No. of subs: ${it.size}", Toast.LENGTH_LONG)
                    .show()
                subscriptionListAdapter.submitList(it)
                subscriptionListAdapter.notifyDataSetChanged()
                if (it.isEmpty()) {
                    recyclerView.visibility = View.GONE
                    lottieView.visibility = View.VISIBLE
                } else {
                    recyclerView.visibility = View.VISIBLE
                    lottieView.visibility = View.GONE
                }
            }
        })

        //Observe loading
        subscriptionViewModel.loadingLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it == Loading.FAILED || it == Loading.SUCCESS) {
                    loadingDialog.dismiss()
                }
            }
        })
        return root
    }

    private fun createLoadingDialog(): AlertDialog {
        val layout = LayoutInflater.from(requireContext()).inflate(R.layout.loading_layout, null)
        val builder = AlertDialog.Builder(requireContext(), R.style.TransparentDialog)
        builder.setView(layout)
        builder.setCancelable(false)
        return builder.create()
    }
}