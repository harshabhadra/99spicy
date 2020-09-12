package com.a99Spicy.a99spicy.ui.subscriptions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.a99Spicy.a99spicy.R
import com.a99Spicy.a99spicy.ui.HomeActivity


class SubscriptionFragment : Fragment() {

    private lateinit var subscriptionViewModel: SubscriptionViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        subscriptionViewModel =
            ViewModelProviders.of(this).get(SubscriptionViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)

        val activity = activity as HomeActivity
        activity.setAppBarElevation(10F)
        activity.setToolbarTitle(getString(R.string.subscriptions))
        activity.setToolbarLogo(null)
        return root
    }
}