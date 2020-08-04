package com.a99Spicy.a99spicy.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.a99Spicy.a99spicy.R
import com.a99Spicy.a99spicy.databinding.FragmentProfileBinding
import com.a99Spicy.a99spicy.ui.HomeActivity
import com.a99Spicy.a99spicy.utils.AppUtils

class ProfileFragment : Fragment() {

    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var profileFragmentBinding:FragmentProfileBinding
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
        profileFragmentBinding = FragmentProfileBinding.inflate(inflater,container,false)

        val activity = activity as HomeActivity
        activity.setAppBarElevation(0F)
        activity.setToolbarTitle(getString(R.string.title_profile))
        activity.setToolbarLogo(null)

        //Setting up profile RecyclerView
        profileItemsAdapter = ProfileItemsAdapter(ProfileItemClickListener {

        })
        profileFragmentBinding.profileRecycler.adapter = profileItemsAdapter
        profileItemsAdapter.setProfileNameList(AppUtils.getProfileItemsList(requireContext()).toMutableList())

        return profileFragmentBinding.root
    }
}