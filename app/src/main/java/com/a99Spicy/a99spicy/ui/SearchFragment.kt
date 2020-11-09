package com.a99Spicy.a99spicy.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.a99Spicy.a99spicy.MyApplication
import com.a99Spicy.a99spicy.R
import com.a99Spicy.a99spicy.domain.DomainProduct
import com.a99Spicy.a99spicy.network.Profile
import com.a99Spicy.a99spicy.ui.products.ProductListAdapter
import com.a99Spicy.a99spicy.ui.products.ProductListViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.search_fragment.view.*
import timber.log.Timber
import java.util.*

class SearchFragment : Fragment(), ProductListAdapter.OnProductItemClickListener,
    ProductListAdapter.OnProductMinusClickListener {

    private lateinit var productListAdapter: ProductListAdapter
    private lateinit var viewModel: SearchViewModel
    private var searchPList: MutableList<DomainProduct> = mutableListOf()
    private lateinit var profile: Profile
    private var fProductList: MutableSet<DomainProduct> = mutableSetOf()
    private var index = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.search_fragment, container, false)

        val arguments = SearchFragmentArgs.fromBundle(requireArguments())
        val name = arguments.name.toLowerCase(Locale.getDefault())
        val productList = arguments.products.productList
        profile = arguments.profile
        val cityName = profile.billing.address1
        fProductList.clear()
        for (product in productList) {
            val metaList = product.metaData
            for (meta in metaList) {
                if (meta.key.toLowerCase(Locale.getDefault()) == "_${cityName}_sale_price") {
                    index = metaList.indexOf(meta)
                    Timber.e("index no. : $index")
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

        val application = requireNotNull(this.activity).application as MyApplication
        val productListViewModelFactory = ProductListViewModelFactory(application)
        //Setting up recyclerview
        productListAdapter = ProductListAdapter(
            null, null, productListViewModelFactory,
            this, this, this
        )
        val recyclerView = view.search_recyclerView
        recyclerView.adapter = productListAdapter
        val list = getSearchProductList(name, fProductList.toList())
        if (!list.isNullOrEmpty()) {
            recyclerView.visibility = View.VISIBLE
            view.empty_search_view.visibility = View.GONE
            productListAdapter.submitList(list)
            productListAdapter.notifyDataSetChanged()
            Timber.e("no. of products: ${list.size}, product name: ${list[0].name}")
        } else {
            Toast.makeText(requireContext(), "No Products Found for your Search", Toast.LENGTH_LONG)
                .show()
            recyclerView.visibility = View.GONE
            view.empty_search_view.visibility = View.VISIBLE
        }
        return view
    }

    private fun getSearchProductList(
        name: String,
        productList: List<DomainProduct>
    ): List<DomainProduct> {
        for (product in productList) {
            if (product.name.toLowerCase(Locale.getDefault()).contains(name)) {
                searchPList.add(product)
            }
        }
        return searchPList
    }

    override fun onProductItemClick(position: Int, quantity: Int) {
        val snackBar = Snackbar.make(
            requireView().search_layout,
            "Cart Updated",
            Snackbar.LENGTH_INDEFINITE
        )
        snackBar.setAction("Order Now", View.OnClickListener {
            findNavController().navigate(
                SearchFragmentDirections
                    .actionSearchFragmentToCartFragment(profile)
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
            requireView().search_layout,
            "Cart Updated",
            Snackbar.LENGTH_INDEFINITE
        )
        snackBar.setAction("Order Now", View.OnClickListener {
            findNavController().navigate(
                SearchFragmentDirections
                    .actionSearchFragmentToCartFragment(profile)
            )
        })
        snackBar.animationMode = Snackbar.ANIMATION_MODE_FADE
        snackBar.setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
        snackBar.setTextColor(Color.WHITE)
        snackBar.setActionTextColor(Color.WHITE)
        snackBar.show()
    }

}