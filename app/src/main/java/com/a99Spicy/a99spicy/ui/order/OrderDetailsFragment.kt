package com.a99Spicy.a99spicy.ui.order

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
import com.a99Spicy.a99spicy.MyApplication
import com.a99Spicy.a99spicy.R
import com.a99Spicy.a99spicy.database.DatabaseCoupon
import com.a99Spicy.a99spicy.databinding.FragmentOrderDetailsBinding
import com.a99Spicy.a99spicy.network.Coupon
import com.a99Spicy.a99spicy.network.ResponseLineItem
import com.a99Spicy.a99spicy.ui.HomeActivity
import com.anupkumarpanwar.scratchview.ScratchView
import kotlinx.android.synthetic.main.scratch_coupon_layout.view.*
import timber.log.Timber
import kotlin.random.Random

class OrderDetailsFragment : Fragment() {

    private lateinit var orderDetailsFragmentBinding: FragmentOrderDetailsBinding
    private lateinit var orderDetailsListAdapter: OrderDetailsListAdapter
    private lateinit var lineItemsList: List<ResponseLineItem>
    private lateinit var sender: String
    private lateinit var loadingDialog: AlertDialog
    private lateinit var viewModel: OrderDetailsViewModel
    private var fCouponList: MutableSet<DatabaseCoupon> = mutableSetOf()
    private lateinit var userId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        orderDetailsFragmentBinding =
            FragmentOrderDetailsBinding.inflate(inflater, container, false)

        val application = requireNotNull(this.activity).application as MyApplication
        val viewModelFactory = OrderDetailsViewModelFactory(application)
        viewModel =
            ViewModelProvider(this, viewModelFactory).get(OrderDetailsViewModel::class.java)

        val arguments = OrderDetailsFragmentArgs.fromBundle(requireArguments())
        sender = arguments.sender
        orderDetailsFragmentBinding.order = arguments.order
        lineItemsList = arguments.order.lineItems

        val activity = activity as HomeActivity
        userId = activity.getUserId()

        orderDetailsListAdapter = OrderDetailsListAdapter()
        orderDetailsFragmentBinding.orderDetailsRecyclerView.adapter = orderDetailsListAdapter
        orderDetailsListAdapter.submitList(lineItemsList)

        if (sender == getString(R.string.title_cart)) {
            loadingDialog = createLoadingDialog()
            loadingDialog.show()
            viewModel.getAllCoupons()
        }

        //Observe coupons live data
        viewModel.couponLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it.isNotEmpty()) {
                    checkIfUsed(it)
                }
            }
        })

        //Observe loading liveData
        viewModel.couponLoadingLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it == CouponLoading.SUCCESS || it == CouponLoading.FAILED) {
                    loadingDialog.dismiss()
                }
            }
        })
        return orderDetailsFragmentBinding.root
    }

    private fun checkIfUsed(coupons: List<Coupon>) {
        for (coupon in coupons) {
            val usedList = coupon.used_by
            usedList?.let {
                if (!it.contains(userId)) {
                    fCouponList.add(
                        DatabaseCoupon(
                            coupon.id,
                            coupon.code,
                            coupon.amount,
                            coupon.dateCreated,
                            coupon.dateCreatedGmt,
                            coupon.dateModified,
                            coupon.dateModifiedGmt,
                            coupon.discountType,
                            coupon.description,
                            coupon.dateExpires,
                            coupon.dateExpiresGmt,
                            coupon.usageCount,
                            coupon.individualUse,
                            coupon.minimumAmount,
                            coupon.maximumAmount,
                            coupon.product_categories
                        )
                    )
                }
            } ?: fCouponList.add(
                DatabaseCoupon(
                    coupon.id,
                    coupon.code,
                    coupon.amount,
                    coupon.dateCreated,
                    coupon.dateCreatedGmt,
                    coupon.dateModified,
                    coupon.dateModifiedGmt,
                    coupon.discountType,
                    coupon.description,
                    coupon.dateExpires,
                    coupon.dateExpiresGmt,
                    coupon.usageCount,
                    coupon.individualUse,
                    coupon.minimumAmount,
                    coupon.maximumAmount,
                    coupon.product_categories
                )
            )
        }

        if (fCouponList.isNotEmpty()) {
            Timber.e("Final coupon list size: ${fCouponList.size}")
            if (fCouponList.size > 1) {
                generateRandomCoupon(fCouponList.size, fCouponList.toList())
            } else {
                createCouponDialog(fCouponList.toList()[0])
            }
        }
    }

    private fun generateRandomCoupon(size: Int, coupons: List<DatabaseCoupon>) {

        val random = Random
        val num = random.nextInt(size) + 1
        Timber.e("Coupon no. : $num")
        val coupon = coupons[num - 1]
        createCouponDialog(coupon)
    }

    private fun createCouponDialog(coupon: DatabaseCoupon) {
        viewModel.addCoupon(
            coupon
        )
        val random = Random
        var cNo = random.nextInt(4)
        val layout =
            LayoutInflater.from(requireContext()).inflate(R.layout.scratch_coupon_layout, null)
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(layout)
        val couponCodeTv = layout.coupon_code_tv
        val couponDesTv = layout.coupon_des_tv
        val scratchView = layout.scratch_view

        couponCodeTv.text = coupon.code
        couponDesTv.text = coupon.description
        when (cNo) {
            1 -> scratchView.overlay.add(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.coupon_1
                )!!
            )
            2 -> scratchView.overlay.add(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.coupon_2
                )!!
            )
            3 -> scratchView.overlay.add(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.coupon_3
                )!!
            )
            else -> scratchView.overlay.add(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.coupon_4
                )!!
            )
        }

        scratchView.setRevealListener(object : ScratchView.IRevealListener {
            override fun onRevealed(scratchView: ScratchView?) {
                Toast.makeText(
                    requireContext(),
                    "You've Won this Coupon, Use this to For Your next Order",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onRevealPercentChangedListener(scratchView: ScratchView?, percent: Float) {

            }
        })

        val dialog = builder.create()
        dialog.show()
    }

    private fun createLoadingDialog(): AlertDialog {
        val layout = LayoutInflater.from(requireContext()).inflate(R.layout.loading_layout, null)
        val builder = AlertDialog.Builder(requireContext(), R.style.TransparentDialog)
        builder.setView(layout)
        builder.setCancelable(false)
        return builder.create()
    }
}