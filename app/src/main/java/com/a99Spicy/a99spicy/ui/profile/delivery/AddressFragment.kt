package com.a99Spicy.a99spicy.ui.profile.delivery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.a99Spicy.a99spicy.MyApplication
import com.a99Spicy.a99spicy.R
import com.a99Spicy.a99spicy.database.DatabaseShipping
import com.a99Spicy.a99spicy.databinding.FragmentAddressBinding
import com.a99Spicy.a99spicy.domain.DeliveryAddress
import com.a99Spicy.a99spicy.domain.LocationDetails
import com.a99Spicy.a99spicy.ui.HomeActivity
import timber.log.Timber

class AddressFragment : Fragment() {

    private lateinit var addressFragmentBinding: FragmentAddressBinding
    private lateinit var viewModel: DeliveryAddressViewModel

    private lateinit var userId: String

    private lateinit var shippingDetails: DatabaseShipping
    private var locationDetail: LocationDetails? = null
    private var address: DeliveryAddress? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        addressFragmentBinding = FragmentAddressBinding.inflate(inflater, container, false)

        val application = requireNotNull(this.activity).application as MyApplication
        val viewModelFactory = DeliveryAddressViewModelFactory(application)

        //Initialize ViewModel class
        viewModel =
            ViewModelProvider(this, viewModelFactory).get(DeliveryAddressViewModel::class.java)

        val activity = activity as HomeActivity
        activity.setAppBarElevation(0F)
        activity.setToolbarTitle(getString(R.string.delivery_add))
        activity.setToolbarLogo(null)

        userId = activity.getUserId()
        locationDetail = activity.getLocation()

        locationDetail?.let {
            addressFragmentBinding.location = it
        }

        val arguments = AddressFragmentArgs.fromBundle(requireArguments())
        address = arguments.address

        //Set onClickListener to save address button
        addressFragmentBinding.addressDetailsSaveButton.setOnClickListener {

            val firstName = addressFragmentBinding.addressNameTextInput.text.toString()
            val city = addressFragmentBinding.shopCityTextInput.text.toString()
            val postCode = addressFragmentBinding.shopPostCodeTextInput.text.toString()
            val state = addressFragmentBinding.shopStateTextInput.text.toString()
            val mobile = addressFragmentBinding.phoneNumberTextInput.text.toString()
            val flatNo = addressFragmentBinding.flatNoTextInput.text.toString()
            val building = addressFragmentBinding.shopBuildingNoTextInput.text.toString()
            val locality = addressFragmentBinding.shopLocalityTextInput.text.toString()
            val landMark = addressFragmentBinding.shopLandmarkTextInput.text.toString()

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
                else -> {
                    val address =
                        "Mobile: $mobile, Flat no.: $flatNo, Building info: $building, locality: $locality, Landmark:$landMark"

                    Timber.e("Address: Mobile: $mobile, Flat no.: $flatNo, Building info: $building, locality: $locality, Landmark:$landMark\"\n")
                    shippingDetails =
                        DatabaseShipping(
                            firstName = firstName,
                            lastName = "",
                            company = "",
                            address1 = address,
                            address2 = "",
                            city = city,
                            postcode = postCode,
                            country = "India",
                            state = state
                        )
                    viewModel.addAddress(shippingDetails)
                    Timber.e("Saving Address")
                    findNavController().navigate(
                        AddressFragmentDirections.actionAddressFragmentToDeliveryAddressFragment(
                            null, getString(R.string.delivery_add)
                        )
                    )
                }
            }
        }

        return addressFragmentBinding.root
    }
}