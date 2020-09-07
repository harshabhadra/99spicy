package com.a99Spicy.a99spicy.ui.home

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.a99Spicy.a99spicy.MyApplication
import com.a99Spicy.a99spicy.R
import com.a99Spicy.a99spicy.databinding.FragmentHomeBinding
import com.a99Spicy.a99spicy.domain.*
import com.a99Spicy.a99spicy.network.Shipping
import com.a99Spicy.a99spicy.ui.HomeActivity
import com.a99Spicy.a99spicy.utils.AppUtils
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import timber.log.Timber

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var homeFragmentBinding: FragmentHomeBinding
    private var page = 1

    private var mainCatList: MutableList<DomainCategoryItem> = mutableListOf()
    private var catList: MutableList<DomainCategoryItem> = mutableListOf()
    private var subCategoryList: MutableSet<DomainCategoryItem> = mutableSetOf()
    private lateinit var loadingDialog: AlertDialog
    private var locationDetails: LocationDetails? = null
    private lateinit var userId: String
    private var shipping: Shipping? = null
    private lateinit var productList: List<DomainProduct>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val application = requireNotNull(this.activity).application as MyApplication
        val homeViewModelFactory = HomeViewModelFactory(application)
        //Initializing ViewModel class
        homeViewModel =
            ViewModelProvider(this, homeViewModelFactory).get(HomeViewModel::class.java)

        //Inflating layout
        homeFragmentBinding = FragmentHomeBinding.inflate(inflater, container, false)

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

        //Getting user profile
        if (userId.isNotEmpty()) {
            loadingDialog = createLoadingDialog()
            loadingDialog.show()
            homeViewModel.getProfile(userId)
        }
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
                        DomainProducts(productList)
                    )
                )

        })
        homeFragmentBinding.categoryRecyclerView.adapter = homeCategoryAdapter

        //Observe product list
        homeViewModel.productListLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                productList = it
            }
        })

        //Observe category list from ViewModel
        homeViewModel.categoriesLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                mainCatList.clear()
                Timber.e("list size: ${it.size}")
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

        //Observing profile liveData
        homeViewModel.profileLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                shipping = it.shipping
                if (shipping?.address1!!.isNotEmpty() || shipping?.address1 != "") {
                    homeFragmentBinding.homeDeliveryLocationTextView.text = "${shipping?.postcode} ${shipping?.city}"
                }
            }
        })

        //Observe loading livedata
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
                HomeFragmentDirections.actionNavigationHomeToDeliveryAddressFragment(
                    shipping,
                    getString(R.string.title_home)
                )
            )
        }
        setHasOptionsMenu(true)
        return homeFragmentBinding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.action_cart) {
            findNavController()
                .navigate(HomeFragmentDirections.actionNavigationHomeToCartFragment())
        }
        return true
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
}