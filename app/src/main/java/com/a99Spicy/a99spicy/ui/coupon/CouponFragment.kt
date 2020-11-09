package com.a99Spicy.a99spicy.ui.coupon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.a99Spicy.a99spicy.MyApplication
import com.a99Spicy.a99spicy.R
import com.a99Spicy.a99spicy.database.DatabaseCoupon
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.coupon_fragment.view.*
import timber.log.Timber

class CouponFragment(private val clickListener: OnCouponClickListener) :
    BottomSheetDialogFragment() {

    private lateinit var viewModel: CouponViewModel
    private lateinit var couponListAdapter: CouponListAdapter

    interface OnCouponClickListener {
        fun onCouponItemClick(coupon: DatabaseCoupon)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.coupon_fragment, container, false)

        val application = requireNotNull(this.activity).application as MyApplication
        val viewModelFactory = CouponViewModelFactory(application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(CouponViewModel::class.java)

        couponListAdapter = CouponListAdapter(CouponListItemClickListener {
            clickListener.onCouponItemClick(it)
            dismiss()
        })
        view.coupon_recyclerview.adapter = couponListAdapter

        //Observe coupons live data
        viewModel.couponsList.observe(viewLifecycleOwner,Observer {
            it?.let {
                Timber.e("no. of coupons: ${it.size}")
                if (it.isNotEmpty()) {
                    couponListAdapter.submitList(it)
                } else {
                    Toast.makeText(requireContext(), "You Don't have any Coupon", Toast.LENGTH_LONG)
                        .show()
                    dismiss()
                }
            }
        })
        return view
    }

}