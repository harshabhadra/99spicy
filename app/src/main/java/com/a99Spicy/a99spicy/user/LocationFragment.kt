package com.a99Spicy.a99spicy.user

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.a99Spicy.a99spicy.R
import com.a99Spicy.a99spicy.databinding.LocationFragmentBinding
import com.a99Spicy.a99spicy.network.Address
import com.a99Spicy.a99spicy.network.Profile
import com.a99Spicy.a99spicy.network.ShippingDetail
import com.a99Spicy.a99spicy.ui.HomeActivity
import com.a99Spicy.a99spicy.utils.Constants
import java.util.*

class LocationFragment : Fragment() {

    private lateinit var viewModel: LocationViewModel
    private lateinit var locationFragmentBinding: LocationFragmentBinding
    private lateinit var postCode: String
    private lateinit var city: String
    private lateinit var state: String
    private lateinit var locality: String
    private lateinit var name: String
    private lateinit var userId: String
    private lateinit var phone: String
    private lateinit var email: String

    private lateinit var loadingDialog: AlertDialog
    private lateinit var profile: Profile

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //Inflating layout
        locationFragmentBinding = LocationFragmentBinding.inflate(inflater, container, false)

        //Initializing ViewModel
        viewModel = ViewModelProvider(this).get(LocationViewModel::class.java)

        val arguments = LocationFragmentArgs.fromBundle(requireArguments())
        userId = arguments.userId
        name = arguments.name
        phone = arguments.phone

        if (foregroundPermissionApproved()) {
            getLocationDetails()
        }

        //Set onClickListener to submit button
        locationFragmentBinding.signUpSubmitButton.setOnClickListener {

            email = locationFragmentBinding.signUpEmailTextInput.text.toString()
            postCode = locationFragmentBinding.signUpPostCodeTextInput.text.toString()
            city = locationFragmentBinding.signUpCityTextInput.text.toString()
            state = locationFragmentBinding.signUpStateTextInput.text.toString()
            locality = locationFragmentBinding.signUpLocalityTextInput.text.toString()

            when {
                postCode.isEmpty() -> {
                    Toast.makeText(
                        requireContext(),
                        "Enter Postcode",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                city.isEmpty() -> {
                    Toast.makeText(
                        requireContext(),
                        "Enter City",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                state.isEmpty() -> {
                    Toast.makeText(
                        requireContext(),
                        "Enter state",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                locality.isEmpty() -> {
                    Toast.makeText(
                        requireContext(),
                        "Enter locality",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                email.isEmpty()->{
                    Toast.makeText(
                        requireContext(),
                        "Enter Email",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    val shipping = ShippingDetail(
                        name, "", "", locality, "", city, postCode, "India",
                        state,phone, email
                    )
                    viewModel.setAddress(userId.toInt(), Address(shipping))
                    loadingDialog = createLoadingDialog()
                    loadingDialog.show()
                }
            }
        }

        //Observe address liveData from ViewModel class
        viewModel.addressLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let {
                val sharedPreferences = requireActivity().getSharedPreferences(
                    Constants.LOG_IN,
                    Context.MODE_PRIVATE
                )
                val editor = sharedPreferences.edit()
                editor.putBoolean(Constants.IS_LOG_IN, true)
                editor.putString(Constants.SAVED_USER_ID, userId)
                editor.putString(Constants.PHONE, phone)
                editor.apply()
                profile = it
                goToHome(userId, it)
            }
        })

        //Observe loading liveData
        viewModel.loadingLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let {
                if (it == LocationLoading.SUCCESS || it == LocationLoading.FAILED) {
                    loadingDialog.dismiss()
                }
            }
        })
        return locationFragmentBinding.root
    }

    private fun foregroundPermissionApproved(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    @SuppressLint("MissingPermission")
    private fun getLocationDetails() {

        val locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (isGpsEnabled) {
            val location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            location?.let {

                val geocoder = Geocoder(requireContext(), Locale.getDefault())
                val addressList = geocoder.getFromLocation(it.latitude, it.longitude, 1)

                val postCode = addressList[0].postalCode
                val city = addressList[0].locality
                val district = addressList[0].subAdminArea
                val state = addressList[0].adminArea

                locationFragmentBinding.signUpPostCodeTextInput.setText(postCode)
                locationFragmentBinding.signUpCityTextInput.setText(city)
                locationFragmentBinding.signUpStateTextInput.setText(state)
            }
        } else {
            //Create an alert dialog if the location is off on device
            val builder =
                AlertDialog.Builder(requireContext())
            builder.setCancelable(false)
            builder.setMessage("Your Location is disabled. Turn on your location")
            builder.setPositiveButton(
                "ok"
            ) { dialog, which -> startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }

            val dialog = builder.create()
            dialog.show()
        }
    }

    private fun createLoadingDialog(): AlertDialog {
        val layout = LayoutInflater.from(requireContext()).inflate(R.layout.loading_layout, null)
        val builder = AlertDialog.Builder(requireContext(), R.style.TransparentDialog)
        builder.setView(layout)
        builder.setCancelable(false)
        return builder.create()
    }

    private fun goToHome(userId: String, profile: Profile?) {
        val intent = Intent(requireActivity(), HomeActivity::class.java)
        intent.putExtra(Constants.USER_ID, userId)
        intent.putExtra(Constants.PROFILE, profile)
        startActivity(intent)
        requireActivity().finish()
    }
}