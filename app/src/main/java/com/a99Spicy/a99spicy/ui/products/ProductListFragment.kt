package com.a99Spicy.a99spicy.ui.products

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.a99Spicy.a99spicy.MyApplication
import com.a99Spicy.a99spicy.R
import com.a99Spicy.a99spicy.databinding.ProductListFragmentBinding
import com.a99Spicy.a99spicy.domain.DomainProduct
import com.a99Spicy.a99spicy.network.Profile
import com.a99Spicy.a99spicy.ui.HomeActivity
import com.a99Spicy.a99spicy.utils.CountDrawable
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import timber.log.Timber

class ProductListFragment : Fragment(), ProductListAdapter.OnProductItemClickListener,
    ProductListAdapter.OnProductMinusClickListener {

    private lateinit var viewModel: ProductListViewModel
    private lateinit var productListFragmentBinding: ProductListFragmentBinding
    private lateinit var profile: Profile

    private lateinit var productList: List<DomainProduct>
    private lateinit var catName: String
    private var cartCount = 1

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

        profile = arguments.profile
        catName = arguments.catname
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
            catName,
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

        //Observe cart items liveData
        viewModel.cartItemsLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                cartCount = it.size
                Timber.e("Cart items: $cartCount")
                productListFragmentBinding.productCartFab.count = it.size
            }
        })

        //Set onClickListener to cart fab
        productListFragmentBinding.productCartFab.setOnClickListener {
            findNavController().navigate(
                ProductListFragmentDirections.actionProductListFragmentToCartFragment(
                    profile
                )
            )
        }

        //Attatching tablayout with viewPager
        TabLayoutMediator(productListFragmentBinding.categoryTabLayout,
            productListFragmentBinding.categoryViewPager,
            TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                val listSize = catList.size
                for (i in 0 until listSize) {
                    tab.text = catList[position].catName
                }
            }).attach()

//        setHasOptionsMenu(true)
        return productListFragmentBinding.root
    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        inflater.inflate(R.menu.product_menu, menu)
//    }
//
//    override fun onPrepareOptionsMenu(menu: Menu) {
//        setCount(requireContext(), cartCount.toString(), menu)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        if (item.itemId == R.id.action_product_cart) {
//            findNavController().navigate(
//                ProductListFragmentDirections.actionProductListFragmentToCartFragment(
//                    profile
//                )
//            )
//        }
//        return true
//    }

    override fun onProductItemClick(position: Int, quantity: Int) {
        val snackBar = Snackbar.make(
            productListFragmentBinding.productRootLayout,
            "Cart Updated",
            Snackbar.LENGTH_INDEFINITE
        )
        snackBar.setAction("Order Now", View.OnClickListener {
            findNavController().navigate(
                ProductListFragmentDirections
                    .actionProductListFragmentToCartFragment(profile)
            )
        })
        snackBar.animationMode = Snackbar.ANIMATION_MODE_FADE
        snackBar.setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
        snackBar.setTextColor(Color.WHITE)
        snackBar.setActionTextColor(Color.WHITE)
        snackBar.show()
    }

    override fun onProductMinusClick(position: Int, quantity: Int) {
        val snackBar = Snackbar.make(
            productListFragmentBinding.productRootLayout,
            "Cart Updated",
            Snackbar.LENGTH_INDEFINITE
        )
        snackBar.setAction("Order Now", View.OnClickListener {
            findNavController().navigate(
                ProductListFragmentDirections
                    .actionProductListFragmentToCartFragment(profile)
            )
        })
        snackBar.animationMode = Snackbar.ANIMATION_MODE_FADE
        snackBar.setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
        snackBar.setTextColor(Color.WHITE)
        snackBar.setActionTextColor(Color.WHITE)
        snackBar.show()
    }

    //Get products by category
    private fun getProductsByCategory(catId: Int) {
        val (match, rest) = productList.partition {
            it.categories[0].id == catId || it.categories[1].id == catId
        }
        Timber.e("Match list size: ${match.size}")
        if (match.isNotEmpty()) {
            viewModel.setCategoryProductList(match)
        }
    }

    private fun setCount(context: Context, count: String, menu: Menu) {
        val menuItem: MenuItem = menu.findItem(R.id.action_product_cart)
        val icon = menuItem.icon as LayerDrawable
        val badge: CountDrawable

        // Reuse drawable if possible
        val reuse = icon.findDrawableByLayerId(R.id.ic_group_count)
        badge = if (reuse != null && reuse is CountDrawable) {
            reuse
        } else {
            CountDrawable(context)
        }
        badge.setCount(count)
        icon.mutate()
        icon.setDrawableByLayerId(R.id.ic_group_count, badge)
    }
}