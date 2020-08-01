package com.a99Spicy.a99spicy.ui.cart

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


class CartFragment : Fragment() {

    private lateinit var cartViewModel: CartViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        cartViewModel =
            ViewModelProviders.of(this).get(CartViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        val textView: TextView = root.findViewById(R.id.text_dashboard)
        cartViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        val activity = activity as HomeActivity
        activity.setAppBarElevation(10F)
        activity.setToolbarTitle(getString(R.string.title_cart))
        activity.setToolbarLogo(null)
        return root
    }
}