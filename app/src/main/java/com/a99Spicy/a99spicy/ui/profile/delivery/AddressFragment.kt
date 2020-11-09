package com.a99Spicy.a99spicy.ui.profile.delivery

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.a99Spicy.a99spicy.MainActivity
import com.a99Spicy.a99spicy.R
import com.a99Spicy.a99spicy.databinding.FragmentAddressBinding
import com.a99Spicy.a99spicy.network.Address
import com.a99Spicy.a99spicy.network.Billing
import com.a99Spicy.a99spicy.network.Shipping
import com.a99Spicy.a99spicy.network.ShippingDetail
import com.a99Spicy.a99spicy.ui.HomeActivity
import timber.log.Timber

class AddressFragment : Fragment() {

    private lateinit var addressFragmentBinding: FragmentAddressBinding
    private lateinit var viewModel: AddressViewModel
    private lateinit var userId: String
    private lateinit var shippingDetails: ShippingDetail
    private var address: Billing? = null
    private lateinit var loadingDialog: AlertDialog
    private lateinit var phone: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        addressFragmentBinding = FragmentAddressBinding.inflate(inflater, container, false)

        //Initialize ViewModel class
        viewModel =
            ViewModelProvider(this).get(AddressViewModel::class.java)

        val activity = activity as HomeActivity
        activity.setAppBarElevation(0F)
        activity.setToolbarTitle(getString(R.string.delivery_add))
        activity.setToolbarLogo(null)
        userId = activity.getUserId()

        val arguments = AddressFragmentArgs.fromBundle(requireArguments())
        address = arguments.shipping
        phone = arguments.phone
        addressFragmentBinding.location = address

        addressFragmentBinding.phoneNumberTextInput.setText(phone)

        //Set onClickListener to save address button
        addressFragmentBinding.addressDetailsSaveButton.setOnClickListener {

            val firstName = addressFragmentBinding.addressNameTextInput.text.toString()
            val email = addressFragmentBinding.addressEmailTextInput.text.toString()
            val city = addressFragmentBinding.shopCityTextInput.text.toString()
            val postCode = addressFragmentBinding.shopPostCodeTextInput.text.toString()
            val state = addressFragmentBinding.shopStateTextInput.text.toString()
            val mobile = addressFragmentBinding.phoneNumberTextInput.text.toString()
            val locality = addressFragmentBinding.shopLocalityTextInput.text.toString()
            val address = addressFragmentBinding.shopAddressTextInput.text.toString()

            when {
                firstName.isEmpty() -> {
                    Toast.makeText(
                        requireContext(),
                        "Enter Your Name",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                mobile.isEmpty() -> {
                    Toast.makeText(
                        requireContext(),
                        "Enter Mobile Number",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                email.isEmpty()->{
                    Toast.makeText(
                        requireContext(),
                        "Enter Email Id",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                locality.isEmpty() -> {
                    Toast.makeText(
                        requireContext(),
                        "Enter info about your locality",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                postCode.isEmpty() -> {
                    Toast.makeText(
                        requireContext(),
                        "Enter you Post code",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                city.isEmpty() -> {
                    Toast.makeText(
                        requireContext(),
                        "Enter you City/Town name",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                state.isEmpty() -> {
                    Toast.makeText(
                        requireContext(),
                        "Enter State",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                address.isEmpty() -> {
                    Toast.makeText(requireContext(), "Enter Address", Toast.LENGTH_SHORT).show()
                }
                else -> {

                    Timber.e("Address: Mobile: $mobile, locality: $locality")
                    shippingDetails =
                        ShippingDetail(
                            first_name = firstName,
                            last_name = "",
                            company = "",
                            address_1 = locality,
                            address_2 = address,
                            city = city,
                            postcode = postCode,
                            country = "India",
                            state = state,
                            phone = mobile,
                            email = email
                        )
                    Timber.e("Saving Address")
                    viewModel.updateShipping(userId, Address(shippingDetails))
                    loadingDialog = createLoadingDialog()
                    loadingDialog.show()
                }
            }
        }

        //Observe shipping update response
        viewModel.updateShippingLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                val intent = Intent(activity, MainActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
        })

        //observe loading state
        viewModel.loadingLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it == AddressLoading.FAILED || it == AddressLoading.SUCCESS) {
                    loadingDialog.dismiss()
                }
            }
        })
        return addressFragmentBinding.root
    }

    private fun createLoadingDialog(): AlertDialog {
        val layout = LayoutInflater.from(requireContext()).inflate(R.layout.loading_layout, null)
        val builder = AlertDialog.Builder(requireContext(), R.style.TransparentDialog)
        builder.setView(layout)
        builder.setCancelable(false)
        return builder.create()
    }
}