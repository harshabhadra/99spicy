package com.a99Spicy.a99spicy.ui.products

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.a99Spicy.a99spicy.MyApplication
import com.a99Spicy.a99spicy.R
import com.a99Spicy.a99spicy.databinding.ProductListFragmentBinding
import com.a99Spicy.a99spicy.domain.DomainProduct
import com.a99Spicy.a99spicy.domain.DomainProducts
import com.a99Spicy.a99spicy.ui.HomeActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import timber.log.Timber

class ProductListFragment : Fragment(), ProductListAdapter.OnProductItemClickListener,
    ProductListAdapter.OnProductMinusClickListener {

    private lateinit var viewModel: ProductListViewModel
    private lateinit var productListFragmentBinding: ProductListFragmentBinding

    private lateinit var productList: List<DomainProduct>
    private var qty = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //Inflating Layout
        productListFragmentBinding = ProductListFragmentBinding.inflate(inflater, container, false)
        val application = requireNotNull(this.activity).application as MyApplication
        val productListViewModelFactory = ProductListViewModelFactory(application)
        viewModel = ViewModelProvider(
            this,
            productListViewModelFactory
        ).get(ProductListViewModel::class.java)

        val arguments = ProductListFragmentArgs.fromBundle(requireArguments())
        val catList = arguments.subCategories.categoryList
        val products = arguments.products.productList
        val (match, rest) = products.partition {
            it.name == "Wallet Topup"
        }
        productList = rest

        Timber.e("total no. of products: ${productList.size}")
        Timber.e("Categories : ${catList.size}")

        val activity = activity as HomeActivity
        activity.setToolbarLogo(null)
        activity.setToolbarTitle(arguments.catname)
        activity.setAppBarElevation(0f)

        //Setting up the viewPager
        val productCategoryAdapter = ProductCategoryAdapter(
            this,
            viewLifecycleOwner, this, this
        )
        productListFragmentBinding.categoryViewPager.adapter = productCategoryAdapter

        viewModel.resetProductsList()
        for (cat in catList) {
            Timber.e("category : ${cat.catId}")
            getProductsByCategory(cat.catId)
        }

        //Observing products by category
        viewModel.productsByCategoryLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                Timber.e("no. of products: ${it.size}")
                productCategoryAdapter.submitList(it)
                productCategoryAdapter.notifyDataSetChanged()
            }
        })

        TabLayoutMediator(productListFragmentBinding.categoryTabLayout,
            productListFragmentBinding.categoryViewPager,
            TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                val listSize = catList.size
                for (i in 0 until listSize) {
                    tab.text = catList[position].catName
                }
            }).attach()
        return productListFragmentBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    private fun createLoadingDialog(): AlertDialog {
        val layout = LayoutInflater.from(requireContext()).inflate(R.layout.loading_layout, null)
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(layout)
        builder.setCancelable(false)
        return builder.create()
    }

    override fun onProductItemClick(position: Int, quantity: Int) {
        qty += quantity
        val snackBar = Snackbar.make(
            productListFragmentBinding.productRootLayout,
            "Cart Updated",
            Snackbar.LENGTH_INDEFINITE
        )
        snackBar.setAction("Order Now", View.OnClickListener {
            findNavController().navigate(
                ProductListFragmentDirections
                    .actionProductListFragmentToCartFragment()
            )
        })
        snackBar.animationMode = Snackbar.ANIMATION_MODE_FADE
        snackBar.setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
        snackBar.setTextColor(Color.WHITE)
        snackBar.setActionTextColor(Color.WHITE)
        snackBar.show()
    }

    override fun onProductMinusClick(position: Int, quantity: Int) {
        qty -= quantity
        val snackBar = Snackbar.make(
            productListFragmentBinding.productRootLayout,
            "$qty Items",
            Snackbar.LENGTH_INDEFINITE
        )
        snackBar.setAction("Order Now", View.OnClickListener {
            findNavController().navigate(
                ProductListFragmentDirections
                    .actionProductListFragmentToCartFragment()
            )
        })
        snackBar.animationMode = Snackbar.ANIMATION_MODE_SLIDE
        snackBar.setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
        snackBar.setTextColor(Color.WHITE)
        snackBar.setActionTextColor(Color.WHITE)
        snackBar.show()
    }

    private fun getProductsByCategory(catId: Int) {
        val (match, rest) = productList.partition {
            it.categories[0].id == catId || it.categories[1].id == catId
        }
        Timber.e("Match list size: ${match.size}")
        if (match.isNotEmpty()) {
            viewModel.setCategoryProductList(match)
        }
    }
}