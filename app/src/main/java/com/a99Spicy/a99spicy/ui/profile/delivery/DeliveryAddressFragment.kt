package com.a99Spicy.a99spicy.ui.profile.delivery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.a99Spicy.a99spicy.MyApplication
import com.a99Spicy.a99spicy.R
import com.a99Spicy.a99spicy.database.DatabaseShipping
import com.a99Spicy.a99spicy.databinding.DelveryAddressFragmentBinding
import com.a99Spicy.a99spicy.network.Address
import com.a99Spicy.a99spicy.network.Shipping
import com.a99Spicy.a99spicy.network.ShippingDetail
import com.a99Spicy.a99spicy.ui.HomeActivity

class DeliveryAddressFragment : Fragment() {

    private lateinit var viewModel: DeliveryAddressViewModel
    private lateinit var deliveryAddressBinding: DelveryAddressFragmentBinding
    private lateinit var addressListAdapter: AddressListAdapter
    private var shipping: Shipping? = null
    private var address: String = ""
    private lateinit var userId: String
    private lateinit var sender:String

    private lateinit var loadingDialog:AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        deliveryAddressBinding = DelveryAddressFragmentBinding.inflate(inflater, container, false)

        val activity = activity as HomeActivity
        activity.setAppBarElevation(0F)
        activity.setToolbarTitle(getString(R.string.address_book))
        activity.setToolbarLogo(null)
        userId = activity.getUserId()

        val application = requireNotNull(this.activity).application as MyApplication
        val viewModelFactory = DeliveryAddressViewModelFactory(application)
        viewModel =
            ViewModelProvider(this, viewModelFactory).get(DeliveryAddressViewModel::class.java)

        val arguments = DeliveryAddressFragmentArgs.fromBundle(requireArguments())
        shipping = arguments.shipping
        shipping?.let {
            address = it.address1!!
        }
        sender = arguments.sender

        //Setting up address recyclerView
        addressListAdapter = AddressListAdapter(AddressItemClickListener {
            viewModel.removeAddress(
                DatabaseShipping(
                    it.id,
                    it.firstName,
                    it.lastName,
                    it.company,
                    it.address1,
                    it.address2,
                    it.city,
                    it.postcode,
                    it.country,
                    it.state
                )
            )
            addressListAdapter.notifyDataSetChanged()
        })
        deliveryAddressBinding.addressRecyclerView.adapter = addressListAdapter

        //Observing address list liveData
        viewModel.deliveryAddressListLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it.isNotEmpty()) {
                    deliveryAddressBinding.noAddressTextView.visibility = View.GONE
                    addressListAdapter.submitList(it)
                    if (sender == getString(R.string.title_home) || sender == getString(R.string.title_profile)) {
                        if (address == "") {
                            loadingDialog = createLoadingDialog()
                            loadingDialog.show()
                            val add = it[0]
                            viewModel.setAddress(
                                userId.toInt(),
                                Address(
                                    ShippingDetail(
                                        add.firstName,
                                        add.lastName,
                                        add.company,
                                        add.address1,
                                        add.address2,
                                        add.city,
                                        add.postcode,
                                        add.country,
                                        add.state
                                    )
                                )
                            )
                        }
                    } else if (sender == getString(R.string.delivery_add)) {
                        if (shipping == null && it.size == 1) {
                            val add = it[0]
                            loadingDialog = createLoadingDialog()
                            loadingDialog.show()
                            viewModel.setAddress(
                                userId.toInt(),
                                Address(
                                    ShippingDetail(
                                        add.firstName,
                                        add.lastName,
                                        add.company,
                                        add.address1,
                                        add.address2,
                                        add.city,
                                        add.postcode,
                                        add.country,
                                        add.state
                                    )
                                )
                            )
                        }
                    }
                }else{
                    deliveryAddressBinding.noAddressTextView.visibility = View.VISIBLE
                }
            }
        })

        //Observe address response
        viewModel.addressLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                loadingDialog.dismiss()
            }
        })
        //Set onClickListener to add button
        deliveryAddressBinding.addAddressButton.setOnClickListener {
            findNavController().navigate(
                DeliveryAddressFragmentDirections.actionDeliveryAddressFragmentToAddressFragment(
                    null
                )
            )
        }

        return deliveryAddressBinding.root
    }

    private fun createLoadingDialog(): AlertDialog {
        val layout = LayoutInflater.from(requireContext()).inflate(R.layout.loading_layout, null)
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(layout)
        builder.setCancelable(false)
        return builder.create()
    }
}