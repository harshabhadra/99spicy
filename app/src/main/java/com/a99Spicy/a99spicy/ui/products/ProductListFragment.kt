package com.a99Spicy.a99spicy.ui.products

import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
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
import com.a99Spicy.a99spicy.utils.Constants
import com.a99Spicy.a99spicy.utils.CountDrawable
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import java.util.*

class ProductListFragment : Fragment(), ProductListAdapter.OnProductItemClickListener,
    ProductListAdapter.OnProductMinusClickListener {

    private lateinit var viewModel: ProductListViewModel
    private lateinit var productListFragmentBinding: ProductListFragmentBinding
    private lateinit var profile: Profile

    private lateinit var productList: List<DomainProduct>
    private lateinit var catName: String
    private var cartCount = 1
    private var cityName: String = ""
    private var fProductList: MutableSet<DomainProduct> = mutableSetOf()
    private var index: Int = -1
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var phone: String

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
        sharedPreferences =
            requireActivity().getSharedPreferences(Constants.LOG_IN, Context.MODE_PRIVATE)
        phone = sharedPreferences.getString(Constants.PHONE, "")!!
        profile = arguments.profile
        cityName = profile.billing.address1
        catName = arguments.catname.toLowerCase(Locale.getDefault())
        val catList = arguments.subCategories.categoryList
        val products = arguments.products.productList
        val (match, rest) = products.partition {
            it.name == "Wallet Topup"
        }
        productList = rest
        fProductList.clear()
        for (product in productList) {
            val metaList = product.metaData
            for (meta in metaList) {
                if (meta.key.toLowerCase(Locale.getDefault()) == "_${cityName.toLowerCase(Locale.getDefault())}_sale_price") {
                    if (meta.value.isNotEmpty()) {
                        index = metaList.indexOf(meta)
                        val domainP = DomainProduct(
                            product.id,
                            product.name,
                            product.slug,
                            product.dateCreated,
                            product.dateModified,
                            product.status,
                            product.featured,
                            product.description,
                            product.shortDescription,
                            product.sku,
                            product.price,
                            product.regularPrice,
                            product.salePrice,
                            product.onSale,
                            product.purchasable,
                            product.totalSales,
                            product.taxStatus,
                            product.taxClass,
                            product.stockQuantity,
                            product.stockStatus,
                            product.weight,
                            product.reviewsAllowed,
                            product.averageRating,
                            product.ratingCount,
                            product.relatedIds,
                            product.purchaseNote,
                            product.categories,
                            product.images,
                            product.metaData,
                            index
                        )
                        fProductList.add(domainP)
                    }
                }
            }
        }

        val activity = activity as HomeActivity
        activity.setToolbarLogo(null)
        activity.setToolbarTitle(arguments.catname)
        activity.setAppBarElevation(0f)

        //Setting up the viewPager
        val productCategoryAdapter = ProductCategoryAdapter(
            catName, profile,
            this,
            productListViewModelFactory,
            this, this
        )
        productListFragmentBinding.categoryViewPager.adapter = productCategoryAdapter

        viewModel.resetProductsList()
        if (fProductList.isNotEmpty()) {
            for (cat in catList) {

                getProductsByCategory(cat.catId)
            }
        } else {
            MaterialAlertDialogBuilder(requireContext())
                .setMessage(
                    "No products found for your locality ${
                        cityName.toUpperCase(
                            Locale.getDefault()
                        )
                    }. Update your locality & Pin Code in delivery address\nProfile>Address Book"
                )
                .setPositiveButton("Ok", DialogInterface.OnClickListener { dialog, _ ->
                    dialog.dismiss()
                    findNavController().navigate(
                        ProductListFragmentDirections
                            .actionProductListFragmentToAddressFragment(
                                profile.billing,
                                phone
                            )
                    )
                }).show()
        }
        //Observing products by category
        viewModel.productsByCategoryLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                productCategoryAdapter.submitList(it)
                productCategoryAdapter.notifyDataSetChanged()
            }
        })

        //Observe cart items liveData
        viewModel.cartItemsLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                cartCount = it.size
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

        //Attaching tabLayout with viewPager
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.clear()
    }

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
        if (fProductList.isNotEmpty()) {
            val (match, rest) = fProductList.toList().partition {
                it.categories[0].id == catId || it.categories[1].id == catId
            }
            if (match.isNotEmpty()) {
                viewModel.setCategoryProductList(match)
            }
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