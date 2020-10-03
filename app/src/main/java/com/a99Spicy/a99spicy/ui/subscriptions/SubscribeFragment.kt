package com.a99Spicy.a99spicy.ui.subscriptions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.a99Spicy.a99spicy.databinding.SubscribeFragmentBinding

class SubscribeFragment : Fragment() {

    private lateinit var viewModel: SubscribeViewModel
    private lateinit var subscribeBinding: SubscribeFragmentBinding
    private var qty = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        subscribeBinding = SubscribeFragmentBinding.inflate(inflater, container, false)

        //Initializing ViewModel class
        viewModel = ViewModelProvider(this).get(SubscribeViewModel::class.java)

        //Taking arguments
        val arguments = SubscribeFragmentArgs.fromBundle(requireArguments())
        val product = arguments.product
        subscribeBinding.product = product
        return subscribeBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // TODO: Use the ViewModel
    }

}