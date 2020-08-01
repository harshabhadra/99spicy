package com.a99Spicy.a99spicy.ui.products

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.a99Spicy.a99spicy.R
import com.a99Spicy.a99spicy.databinding.ProductListFragmentBinding

class ProductListFragment : Fragment() {

    private lateinit var viewModel: ProductListViewModel
    private lateinit var productListFragmentBinding: ProductListFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //Inflating Layout
        productListFragmentBinding = ProductListFragmentBinding.inflate(inflater,container,false)

        return productListFragmentBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ProductListViewModel::class.java)
        // TODO: Use the ViewModel
    }

}