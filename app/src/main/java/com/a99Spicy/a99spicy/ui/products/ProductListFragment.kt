package com.a99Spicy.a99spicy.ui.products

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.a99Spicy.a99spicy.databinding.ProductListFragmentBinding
import com.a99Spicy.a99spicy.ui.HomeActivity
import com.a99Spicy.a99spicy.utils.AppUtils
import com.google.android.material.tabs.TabLayoutMediator

class ProductListFragment : Fragment() {

    private lateinit var viewModel: ProductListViewModel
    private lateinit var productListFragmentBinding: ProductListFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //Inflating Layout
        productListFragmentBinding = ProductListFragmentBinding.inflate(inflater, container, false)

        val activity = activity as HomeActivity

        val list = AppUtils.getCategoryList(requireContext())

        activity.setToolbarLogo(null)
        activity.setToolbarTitle(list[0].catName)
        activity.setAppBarElevation(0f)
        val productCategoryAdapter = ProductCategoryAdapter(this, viewLifecycleOwner)
        productListFragmentBinding.categoryViewPager.adapter = productCategoryAdapter
        productCategoryAdapter.submitList(list)

        TabLayoutMediator(productListFragmentBinding.categoryTabLayout,
            productListFragmentBinding.categoryViewPager,
            TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                val listSize = list.size
                for (i in 0 until listSize){
                    tab.text = list[position].catName
                }
            }).attach()

        return productListFragmentBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ProductListViewModel::class.java)
    }

}