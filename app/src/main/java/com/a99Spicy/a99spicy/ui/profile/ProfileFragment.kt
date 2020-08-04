package com.a99Spicy.a99spicy.ui.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.a99Spicy.a99spicy.MainActivity
import com.a99Spicy.a99spicy.R
import com.a99Spicy.a99spicy.databinding.FragmentProfileBinding
import com.a99Spicy.a99spicy.ui.HomeActivity
import com.a99Spicy.a99spicy.utils.AppUtils
import com.a99Spicy.a99spicy.utils.Constants

class ProfileFragment : Fragment() {

    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var profileFragmentBinding: FragmentProfileBinding
    private lateinit var profileItemsAdapter: ProfileItemsAdapter

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

        val activity = activity as HomeActivity
        activity.setAppBarElevation(0F)
        activity.setToolbarTitle(getString(R.string.title_profile))
        activity.setToolbarLogo(null)

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
            val sharedPreferences = requireActivity().getSharedPreferences(Constants.LOG_IN,Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putBoolean(Constants.IS_LOG_IN, false)
            editor.apply()

            goToSplash()
        }
        return profileFragmentBinding.root
    }

    private fun navigate(name: String) {

        when (name) {

            getString(R.string.wallet) -> {
                findNavController().
                navigate(ProfileFragmentDirections.actionNavigationNotificationsToWalletFragment())
            }

            getString(R.string.delivery_add) -> {
                findNavController().
                navigate(ProfileFragmentDirections.actionNavigationNotificationsToAddressFragment())
            }
        }
    }

    private fun goToSplash(){
        val intent = Intent(activity,MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }
}