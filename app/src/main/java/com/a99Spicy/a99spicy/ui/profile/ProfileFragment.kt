package com.a99Spicy.a99spicy.ui.profile

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.a99Spicy.a99spicy.MainActivity
import com.a99Spicy.a99spicy.R
import com.a99Spicy.a99spicy.databinding.FragmentProfileBinding
import com.a99Spicy.a99spicy.network.Profile
import com.a99Spicy.a99spicy.ui.HomeActivity
import com.a99Spicy.a99spicy.utils.AppUtils
import com.a99Spicy.a99spicy.utils.Constants
import timber.log.Timber

class ProfileFragment : Fragment() {

    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var profileFragmentBinding: FragmentProfileBinding
    private lateinit var profileItemsAdapter: ProfileItemsAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var phone: String
    private lateinit var profile: Profile

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //Initializing ViewModel class
        profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)

        //Initializing Layout
        profileFragmentBinding = FragmentProfileBinding.inflate(inflater, container, false)

        sharedPreferences =
            requireActivity().getSharedPreferences(Constants.LOG_IN, Context.MODE_PRIVATE)
        phone = sharedPreferences.getString(Constants.PHONE, "")!!
        profileFragmentBinding.userPhoneTextView.text = phone

        val activity = activity as HomeActivity
        activity.setAppBarElevation(0F)
        activity.setToolbarTitle(getString(R.string.title_profile))
        activity.setToolbarLogo(null)
        val userId = activity.getUserId()

        val loadingDialog = createLoadingDialog()
        loadingDialog.show()

        //Get user profile
        profileViewModel.getProfile(userId)

        //Observe Profile LiveData
        profileViewModel.profileLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                Timber.e("Profile ${it.firstName}")
                profile = it
                profileFragmentBinding.user = it
            }
        })

        //Observe Loading LiveData
        profileViewModel.loadingLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it == Loading.SUCCESS) loadingDialog.dismiss()
                else if (it == Loading.FAILED) loadingDialog.dismiss()
            }
        })

        //Setting up profile RecyclerView
        profileItemsAdapter = ProfileItemsAdapter(ProfileItemClickListener {
            navigate(it)
        })
        profileFragmentBinding.profileRecycler.adapter = profileItemsAdapter
        profileItemsAdapter.setProfileNameList(
            AppUtils.getProfileItemsList(requireContext()).toMutableList()
        )

        //Set OnClickListener to sign out button
        profileFragmentBinding.profileSignOutButton.setOnClickListener {

            val editor = sharedPreferences.edit()
            editor.putBoolean(Constants.IS_LOG_IN, false)
            editor.remove(Constants.SAVED_USER_ID)
            editor.remove(Constants.PHONE)
            editor.apply()
            goToSplash()
        }

        return profileFragmentBinding.root
    }

    private fun navigate(name: String) {

        when (name) {

            getString(R.string.wallet) -> {
                findNavController()
                    .navigate(ProfileFragmentDirections.actionNavigationNotificationsToWalletFragment())
            }

            getString(R.string.orders) -> {
                findNavController()
                    .navigate(ProfileFragmentDirections.actionNavigationNotificationsToMyOrdersFragment())
            }
            getString(R.string.delivery_add) -> {
                findNavController().navigate(
                    ProfileFragmentDirections
                        .actionNavigationNotificationsToDeliveryAddressFragment(
                            profile.shipping,
                            getString(R.string.title_profile)
                        )
                )
            }
        }
    }

    private fun goToSplash() {
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun createLoadingDialog(): AlertDialog {
        val layout = LayoutInflater.from(requireContext()).inflate(R.layout.loading_layout, null)
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(layout)
        builder.setCancelable(false)
        return builder.create()
    }
}