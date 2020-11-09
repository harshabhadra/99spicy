package com.a99Spicy.a99spicy.ui.home

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.a99Spicy.a99spicy.MainActivity
import com.a99Spicy.a99spicy.MyApplication
import com.a99Spicy.a99spicy.R
import com.a99Spicy.a99spicy.databinding.FragmentHomeBinding
import com.a99Spicy.a99spicy.domain.DomainCategoryItem
import com.a99Spicy.a99spicy.domain.DomainCategoryItems
import com.a99Spicy.a99spicy.domain.DomainProduct
import com.a99Spicy.a99spicy.domain.DomainProducts
import com.a99Spicy.a99spicy.network.Billing
import com.a99Spicy.a99spicy.network.Profile
import com.a99Spicy.a99spicy.ui.HomeActivity
import com.a99Spicy.a99spicy.utils.AppUtils
import com.a99Spicy.a99spicy.utils.Constants
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import timber.log.Timber
import java.util.*


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var homeFragmentBinding: FragmentHomeBinding

    private var mainCatList: MutableList<DomainCategoryItem> = mutableListOf()
    private var catList: MutableList<DomainCategoryItem> = mutableListOf()
    private var subCategoryList: MutableSet<DomainCategoryItem> = mutableSetOf()
    private lateinit var loadingDialog: AlertDialog
    private lateinit var userId: String
    private var shipping: Billing? = null
    private lateinit var productList: List<DomainProduct>
    private var profile: Profile? = null
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var phone: String
    private var fProductList: MutableList<DomainProduct> = mutableListOf()
    private var cartItems: String = "0"
    private var cityName: String? = null
    private lateinit var filterList: MutableList<DomainProduct>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Timber.e("onCreateView")
        val application = requireNotNull(this.activity).application as MyApplication
        val homeViewModelFactory = HomeViewModelFactory(application)
        //Initializing ViewModel class
        homeViewModel =
            ViewModelProvider(this, homeViewModelFactory).get(HomeViewModel::class.java)

        //Inflating layout
        homeFragmentBinding = FragmentHomeBinding.inflate(inflater, container, false)

        sharedPreferences =
            requireActivity().getSharedPreferences(Constants.LOG_IN, Context.MODE_PRIVATE)
        phone = sharedPreferences.getString(Constants.PHONE, "")!!

        val activity = activity as HomeActivity
        activity.setAppBarElevation(0F)
        activity.setToolbarTitle(getString(R.string.app_name))
        activity.setToolbarLogo(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_action_white_logo
            )
        )
        userId = activity.getUserId()
        profile = activity.getProfile()

        //Getting user profile
        if (userId.isNotEmpty()) {
            Timber.e("User id: $userId")
            loadingDialog = createLoadingDialog()
            loadingDialog.show()
            homeViewModel.getProfile(userId)
        }

        //Observe product list
        homeViewModel.productListLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                productList = it
            }
        })

        //Setting up the newArrival recyclerview
        val newArrivalAdapter = NewArrivalListAdapter(this, viewLifecycleOwner)
        homeFragmentBinding.newArrivalRecyclerView.adapter = newArrivalAdapter
        homeFragmentBinding.newArrivalRecyclerView.recycledViewPool.setMaxRecycledViews(0, 11)
        homeFragmentBinding.newArrivalRecyclerView.setItemViewCacheSize(11)

        //Observing profile liveData
        homeViewModel.profileLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                profile = it
                shipping = it.billing
                cityName = shipping?.address1
                if (shipping?.address1!!.isNotEmpty() || shipping?.address1 != "") {
                    homeFragmentBinding.homeDeliveryLocationTextView.text =
                        "${shipping?.postcode} ${shipping?.address1}"
                    fProductList.clear()
                    filterList = getFilterProducts(productList).toMutableList()
                    when {
                        filterList.isNotEmpty() -> {
                            if (filterList.size > 10) {
                                newArrivalAdapter.submitList(filterList.takeLast(10))
                            } else {
                                newArrivalAdapter.submitList(filterList)

                            }
                        }
                        else -> {
                            newArrivalAdapter.submitList(filterList)
                        }
                    }
                }
            } ?: let {
                MaterialAlertDialogBuilder(requireContext())
                    .setCancelable(false)
                    .setMessage("Failed to Connect with the server. Restart the App")
                    .setPositiveButton("Restart", DialogInterface.OnClickListener { dialog, _ ->
                        dialog.dismiss()
                        val intent = Intent(activity, MainActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    })
                    .show()
            }
        })

        //Setting up HomeSlider
        val homeSliderAdapter = HomeSliderAdapter(AppUtils.getBannerList())
        homeFragmentBinding.homeSlider.setSliderAdapter(homeSliderAdapter)
        homeFragmentBinding.homeSlider.startAutoCycle()
        homeFragmentBinding.homeSlider.setIndicatorAnimation(IndicatorAnimationType.SWAP)
        homeFragmentBinding.homeSlider.setSliderTransformAnimation(SliderAnimations.ZOOMOUTTRANSFORMATION)


        //Setting up Home Category Recyclerview
        val homeCategoryAdapter = HomeCategoryAdapter(HomeCategoryClickListener {
            val id = it.catId
            subCategoryList.clear()
            for (cat in catList) {
                if (cat.parentId == id) {
                    subCategoryList.add(cat)
                }
            }
            findNavController()
                .navigate(
                    HomeFragmentDirections.actionNavigationHomeToProductListFragment(
                        DomainCategoryItems(subCategoryList.toList()),
                        it.catName,
                        DomainProducts(productList),
                        profile!!
                    )
                )

        })
        homeFragmentBinding.categoryRecyclerView.adapter = homeCategoryAdapter

        //Observe category list from ViewModel
        homeViewModel.categoriesLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                mainCatList.clear()
                catList.addAll(it)
                for (cat in it) {
                    if (cat.parentId == 0) {
                        mainCatList.add(cat)
                    }
                }
                homeCategoryAdapter.submitList(mainCatList)
                homeCategoryAdapter.notifyDataSetChanged()
            }
        })


        //Observe loading liveData
        homeViewModel.loadingLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it == HomeLoading.FAILED || it == HomeLoading.SUCCESS) {
                    loadingDialog.dismiss()
                }
            }
        })

        //Set onClickListener to address textView
        homeFragmentBinding.homeDeliveryLocationTextView.setOnClickListener {
            findNavController().navigate(
                HomeFragmentDirections.actionNavigationHomeToAddressFragment(
                    shipping,
                    phone
                )
            )
        }

        //Set onClickListener to cartFab
        homeFragmentBinding.cartFab.setOnClickListener {
            findNavController().navigate(
                HomeFragmentDirections.actionNavigationHomeToCartFragment(
                    profile!!
                )
            )
        }

        //Observe search LiveData
        homeViewModel.searchLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it.isNotEmpty()) {
                    Toast.makeText(requireContext(), "Name: ${it[0].name}", Toast.LENGTH_LONG)
                        .show()
                }
            }
        })

        //Set onClickListener to search button
        homeFragmentBinding.searchImageButton.setOnClickListener {
            val sName = homeFragmentBinding.searchProductEditText.text.toString()
            if (sName.isNotEmpty()) {
                findNavController().navigate(
                    HomeFragmentDirections.actionNavigationHomeToSearchFragment(
                        sName,
                        DomainProducts(productList),
                        profile!!
                    )
                )
            } else {
                Toast.makeText(requireContext(), "Enter Product name to search", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        return homeFragmentBinding.root
    }

    private fun getFilterProducts(products: List<DomainProduct>): List<DomainProduct> {
        cityName?.let {
            Timber.e("city name in home : $it")
            products.forEach { product ->
                val metaList = product.metaData
                for (meta in metaList) {
                    if (meta.key.toLowerCase(Locale.getDefault()) == "_${it.toLowerCase(Locale.getDefault())}_sale_price" && meta.value.isNotEmpty()) {
                        val index = metaList.indexOf(meta)
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
                        Timber.e("Filter product list size: ${fProductList.size}")
                    }
                }
            }
            Timber.e("Final products size:${fProductList.size}")
        } ?: let {
            Timber.e("City name is null")
        }
        return fProductList
    }

    override fun onStart() {
        super.onStart()
        Timber.e("onStart")
    }

    override fun onResume() {
        super.onResume()
        Timber.e("onResume")
        //Observe cart items from ViewModel
        homeViewModel.cartItemListLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                Timber.e("cart items : ${it.size}")
                cartItems = it.size.toString()
                homeFragmentBinding.cartFab.count = it.size
            } ?: let {
                homeFragmentBinding.cartFab.count = 0
            }
        })
    }

    override fun onStop() {
        super.onStop()
        Timber.e("onStop")
    }

    override fun onPause() {
        super.onPause()
        Timber.e("onPause")
    }

    private fun createLoadingDialog(): AlertDialog {
        val layout = LayoutInflater.from(requireContext()).inflate(R.layout.loading_layout, null)
        val builder = AlertDialog.Builder(requireContext(), R.style.TransparentDialog)
        builder.setView(layout)
        builder.setCancelable(false)
        return builder.create()
    }
}