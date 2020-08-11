package com.a99Spicy.a99spicy.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.a99Spicy.a99spicy.BuildConfig
import com.a99Spicy.a99spicy.MyApplication
import com.a99Spicy.a99spicy.R
import com.a99Spicy.a99spicy.databinding.FragmentHomeBinding
import com.a99Spicy.a99spicy.domain.*
import com.a99Spicy.a99spicy.ui.HomeActivity
import com.a99Spicy.a99spicy.utils.AppUtils
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import timber.log.Timber
import java.util.*

class HomeFragment : Fragment() {

    companion object{
        const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34
    }

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var homeFragmentBinding: FragmentHomeBinding
    private lateinit var locationDetails: LocationDetails

    private lateinit var postCode: String
    private lateinit var city: String
    private lateinit var state: String
    private lateinit var district: String

    private lateinit var domainSortedProduct: DomainSortedProducts

    private var locationProductList:MutableSet<DomainProduct> = mutableSetOf()
    private var locationProductSet:MutableSet<DomainProduct> = mutableSetOf()
    private var categorySet: MutableSet<String> = mutableSetOf()

    private var productList:MutableList<DomainProduct> = mutableListOf()

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

        if (foregroundPermissionApproved()) {
            getLocationDetails()
        } else {
            requestForegroundPermissions()
        }

        //Observing Location data
        homeViewModel.locationLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
            }
        })

        //Observing dummyProducts live data from ViewModel class
        homeViewModel.productListLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                Timber.e("Total no. of products: ${it.size}")
                productList = it.toMutableList()
                getListByLocation(productList)
            }
        })

        //Observing Categories from ViewModel class
        homeViewModel.categoryListLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                Timber.e("No. of categories: ${it.size}")
                getSubCategories(locationProductList.toMutableList(),it)
            }
        })

        //Observing Sorted category list from ViewModel
        homeViewModel.sortedCategoryLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                Timber.e("cat name: ${it[0].catName}")
                domainSortedProduct = DomainSortedProducts(it)
                homeViewModel.resetSortedCatList()
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
            findNavController().navigate(HomeFragmentDirections.actionNavigationHomeToProductListFragment())
        })
        homeFragmentBinding.categoryRecyclerView.adapter = homeCategoryAdapter
        homeCategoryAdapter.submitList(AppUtils.getCategoryList(requireContext()))

        setHasOptionsMenu(true)
        return homeFragmentBinding.root
    }

    private fun addToSortedList(cat: String, subCatNameSet:Set<String>?) {
        val domainCategory = DomainSortedCategory(cat,"",subCatNameSet?.toList())
        homeViewModel.setSortedCatList(domainCategory)
    }

    private fun getSubCategories(productList: MutableList<DomainProduct>?, catList: Set<String>) {

        val subNameSet:MutableSet<String> = mutableSetOf()
        productList?.let {
            for (catName in catList) {
                for (product in productList) {
                    val catList = product.categories
                    if (product.categories[1].name == catName) {
                        subNameSet.add(product.categories[0].name)
                    }
                }
                addToSortedList(catName, subNameSet)
                subNameSet.clear()
            }
        }
    }

    private fun getCategoryList(locationProductSet: MutableSet<DomainProduct>) {
        Timber.e("Product list by location: ${locationProductSet.size}")
        for (product in locationProductSet){
            val catList = product.categories
           categorySet.add(catList[1].name)
        }
        homeViewModel.setCategoryList(categorySet)
    }

    private fun getListByLocation(productList: List<DomainProduct>) {

        locationProductList.clear()
        for (product in productList){
            val metaList = product.metaData
            for (meta in metaList){
                if (meta.key.contains("delhi")){
                    locationProductList.add(product)
                }
            }
        }

        getCategoryList(locationProductList)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.action_cart){

            findNavController().navigate(HomeFragmentDirections.actionNavigationHomeToCartFragment())
        }
        return true
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val activity = activity as HomeActivity
        activity.setAppBarElevation(0F)
        activity.setToolbarTitle(getString(R.string.app_name))
        activity.setToolbarLogo(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_action_white_logo
            )
        )

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE -> when {
                grantResults.isEmpty() ->
                    // If user interaction was interrupted, the permission request
                    // is cancelled and you receive empty arrays.
                    Timber.e("User interaction was cancelled.")
                grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
                    // Permission was granted.
                    Toast.makeText(requireContext(), "Permission granted", Toast.LENGTH_SHORT)
                        .show()
                    getLocationDetails()
                }
                else -> {
                    // Permission denied
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setMessage(getString(R.string.permission_denied_explanation))
                    builder.setCancelable(false)
                    builder.setPositiveButton(
                        getString(R.string.settings),
                        DialogInterface.OnClickListener { dialog, which ->
                            // Build intent that displays the App settings screen.
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri = Uri.fromParts(
                                "package",
                                BuildConfig.APPLICATION_ID,
                                null
                            )
                            intent.data = uri
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            dialog.dismiss()
                        })
                    val dialog = builder.create()
                    dialog.show()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocationDetails() {

        val locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (isGpsEnabled) {
            val location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            location?.let {

                val geocoder = Geocoder(requireContext(), Locale.getDefault())
                val addressList = geocoder.getFromLocation(it.latitude, it.longitude, 1)

                postCode = addressList[0].postalCode
                city = addressList[0].locality
                district = addressList[0].subAdminArea
                state = addressList[0].adminArea

                locationDetails = LocationDetails(postCode, city, district, state)
                homeViewModel.setLocationData(locationDetails)
            }
        } else {
            //Create an alert dialog if the location is off on device
            val builder =
                AlertDialog.Builder(requireContext())

            builder.setCancelable(false)
            builder.setMessage("Your Location is disabled. Turn on your location")
            builder.setPositiveButton(
                "ok"
            ) { dialog, which -> startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }

            val dialog = builder.create()
            dialog.show()
        }
    }

    private fun foregroundPermissionApproved(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    private fun requestForegroundPermissions() {
        val provideRationale = foregroundPermissionApproved()

        // If the user denied a previous request, but didn't check "Don't ask again", provide
        // additional rationale.
        if (provideRationale) {
            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage(getString(R.string.permission_rationale))
            builder.setCancelable(false)
            builder.setPositiveButton(getString(R.string.ok),
                DialogInterface.OnClickListener { dialog, which ->
                    // Request permission
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
                    )
                    dialog.dismiss()
                })
            builder.create().show()
        } else {
            Timber.e("Request foreground only permission")
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
            )
        }
    }
}