package com.a99Spicy.a99spicy.ui.cart

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.a99Spicy.a99spicy.MyApplication
import com.a99Spicy.a99spicy.R
import com.a99Spicy.a99spicy.database.DatabaseCart
import com.a99Spicy.a99spicy.database.DatabaseCoupon
import com.a99Spicy.a99spicy.database.asLineItems
import com.a99Spicy.a99spicy.databinding.CartFragmentBinding
import com.a99Spicy.a99spicy.network.*
import com.a99Spicy.a99spicy.ui.HomeActivity
import com.a99Spicy.a99spicy.ui.coupon.CouponFragment
import com.a99Spicy.a99spicy.ui.order.OrderFragment
import com.a99Spicy.a99spicy.ui.order.OrderSlotFragment
import com.a99Spicy.a99spicy.ui.payment.PaymentActivity
import com.a99Spicy.a99spicy.ui.payment.PaymentMethodFragment
import com.a99Spicy.a99spicy.ui.profile.Loading
import com.a99Spicy.a99spicy.utils.AppUtils
import com.a99Spicy.a99spicy.utils.AppUtils.Companion.getCurrentDate
import com.a99Spicy.a99spicy.utils.Constants
import timber.log.Timber
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


class CartFragment : Fragment(), PaymentMethodFragment.OnPaymentMethodClickListener,
    OrderFragment.OnOrderCompleteListener, OrderSlotFragment.OnSlotClickListener,
    CouponFragment.OnCouponClickListener, CartListAdapter.OnCartListClickListener {

    private lateinit var viewModel: CartViewModel
    private lateinit var cartFragmentBinding: CartFragmentBinding
    private lateinit var cartListAdapter: CartListAdapter
    private var totalAmount = 0.00
    private var cartList: MutableSet<DatabaseCart> = mutableSetOf()
    private lateinit var placeOrder: PlaceOrder
    private lateinit var userId: String
    private lateinit var shipping: Billing
    private lateinit var walletBalance: String
    private lateinit var customerNote: String
    private lateinit var profile: Profile
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var phone: String
    private var coupons: MutableSet<CouponLine> = mutableSetOf()
    private var currentCoupon: DatabaseCoupon? = null
    private lateinit var loadingDialog: AlertDialog
    private val catList: MutableList<Int> = mutableListOf()
    private var isCategoryCoupon = false
    private var selectedCouponList: MutableList<DatabaseCoupon> = mutableListOf()
    private var matchItemsAmount = 0.0
    private var couponMoney: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //Inflating layout
        cartFragmentBinding = CartFragmentBinding.inflate(inflater, container, false)

        //Initializing ViewModel class
        val application = requireNotNull(this.activity).application as MyApplication
        val viewModelFactory = CartViewModelFactory(application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(CartViewModel::class.java)

        sharedPreferences =
            requireActivity().getSharedPreferences(Constants.LOG_IN, Context.MODE_PRIVATE)
        phone = sharedPreferences.getString(Constants.PHONE, "")!!

        //Getting values from activity
        val activity = activity as HomeActivity
        activity.setAppBarElevation(0F)
        activity.setToolbarTitle(getString(R.string.title_cart))
        activity.setToolbarLogo(null)
        userId = activity.getUserId()

        //Getting arguments
        val arguments = CartFragmentArgs.fromBundle(requireArguments())
        profile = arguments.profile
        shipping = profile.billing
        cartFragmentBinding.deliveryLocationTextView.text =
            "${shipping.postcode} ${shipping?.address1} ${shipping?.city}"

        //Setting up cart RecyclcerView
        cartFragmentBinding.cartRecyclerView.itemAnimator = null
        //Setting up cart recyclerView
        cartListAdapter = CartListAdapter(CartListItemClickListener {
        }, this, this)
        cartFragmentBinding.cartRecyclerView.adapter = cartListAdapter

        cartFragmentBinding.viewModel = viewModel
        cartFragmentBinding.lifecycleOwner = this
        //Observing Cart Items
        viewModel.cartItemsLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                Timber.e("no. of items in cart: ${it.size}")
                if (it.isNotEmpty()) {
                    var total = 0.0
                    cartFragmentBinding.cartEmptyTextView.visibility = View.GONE
                    cartList = it.toMutableSet()
                    getCatIdList(cartList)
                    for (item in it) {
                        Timber.e("name ${item.salePrice}, quantity: ${item.quantity}")
                        item.salePrice?.let { itemPrice ->
                            if (itemPrice.isNotEmpty()) {
                                total += itemPrice.toDouble().times(item.quantity)
                                Timber.e("Total : $total")
                            }
                        }
                    }

                    Timber.e("Total Amount: $totalAmount")
                    viewModel.setTotal(total.toString())
                    cartListAdapter.submitList(it)
                } else {
                    cartList.clear()
                    cartListAdapter.submitList(it)
                    viewModel.setTotal("00.00")
                    cartFragmentBinding.cartEmptyTextView.visibility = View.VISIBLE
                }
            }
        })

        viewModel.totalLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                totalAmount = it.toDouble()
            }
        })
        //Set onClickListener to placeOrder button
        cartFragmentBinding.placeOrderButton.setOnClickListener {

            if (cartList.isNotEmpty()) {
                val orderSlotFragment = OrderSlotFragment(this)
                orderSlotFragment.show(
                    requireActivity().supportFragmentManager,
                    orderSlotFragment.tag
                )
            } else {
                Toast.makeText(requireContext(), "Add Products To Cart", Toast.LENGTH_SHORT).show()
            }
        }

        //Observe wallet balance
        viewModel.walletBalanceLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                walletBalance = it
                if (walletBalance.toDouble() > totalAmount) {
                    viewModel.cdWallet(
                        userId,
                        WalletRequest(getString(R.string.debit), totalAmount, "checkout")
                    )
                    viewModel.resetWalletBalance()
                } else {
                    loadingDialog.dismiss()
                    Toast.makeText(requireContext(), "Add money to Wallet", Toast.LENGTH_LONG)
                        .show()
                }
            }
        })

        viewModel.walletResponseLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                goToOrder()
                viewModel.resetWallet()
            }
        })

        viewModel.loadingLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it == Loading.SUCCESS || it == Loading.FAILED) {
                    loadingDialog.dismiss()
                }
            }
        })
        //set onClickListener to address tv
        cartFragmentBinding.deliveryLocationTextView.setOnClickListener {
            findNavController().navigate(
                CartFragmentDirections.actionCartFragmentToAddressFragment(
                    shipping,
                    phone
                )
            )
        }

        //Set onClickListener to apply coupon
        cartFragmentBinding.applyCouponTextView.setOnClickListener {
            if (cartList.isNotEmpty() && cartList.size>1) {
                val couponFragment = CouponFragment(this)
                couponFragment.show(activity.supportFragmentManager, couponFragment.tag)
            } else if (cartList.isEmpty()){
                Toast.makeText(
                    requireContext(),
                    "You don't have any products in Cart",
                    Toast.LENGTH_LONG
                ).show()
            }else{
                Toast.makeText(
                    requireContext(),
                    "You should have more than one item to apply coupon",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        return cartFragmentBinding.root
    }

    private fun getCatIdList(cartList: MutableSet<DatabaseCart>) {
        for (item in cartList) {
            catList.add(item.catId)
            catList.add(item.subCatId)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.PLACE_ORDER_REQUEST_CODE) {
            val message = data?.getStringExtra(Constants.MESSAGE)
            message?.let {
                if (it == getString(R.string.success)) {
                    goToOrder()
                } else {
                    Toast.makeText(requireContext(), "Payment Failed, Try Again", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    override fun onPaymentMethodClick(name: String) {
        Timber.e("After payment coupons size: ${coupons.size}")
        if(!isCategoryCoupon){

        }
        if (name == getString(R.string.cash_on_delivery)) {
            placeOrder = PlaceOrder(
                "INR",
                userId.toInt(),
                shipping,
                name,
                "Checkout",
                AppUtils.generatePaytmOrderId(),
                cartList.toList().asLineItems(),
                false,
                customerNote,
                coupons.toList()
            )
        } else {
            placeOrder = PlaceOrder(
                "INR",
                userId.toInt(),
                shipping,
                name,
                "Checkout",
                AppUtils.generatePaytmOrderId(),
                cartList.toList().asLineItems(),
                true,
                customerNote,
                coupons.toList()
            )
        }
        if (name == getString(R.string.credit_card_debit_card_upi) || name == getString(R.string.paytm)) {
            val intent = Intent(activity, PaymentActivity::class.java)
            intent.putExtra(Constants.AMOUNT, totalAmount.toString())
            intent.putExtra(Constants.TRANSACTION_MODE, name)
            intent.putExtra(Constants.ORDER, placeOrder)
            intent.putExtra(Constants.TRANSACTION_TYPE, getString(R.string.place_order))
            startActivityForResult(intent, Constants.PLACE_ORDER_REQUEST_CODE)
        } else if (name == getString(R.string.cash_on_delivery)) {
            goToOrder()
        } else {
            loadingDialog = createLoadingDialog()
            loadingDialog.show()
            viewModel.getWalletBalance(userId)
        }
    }

    private fun goToOrder() {
        val orderFragment = OrderFragment(placeOrder, this)
        orderFragment.show(requireActivity().supportFragmentManager, orderFragment.tag)
    }

    override fun onOrderComplete(orderResponse: OrderResponse) {
        currentCoupon?.let {
            viewModel.removeCoupon(it)
            selectedCouponList.clear()
        }

        viewModel.clearCart().let {
            findNavController().navigate(
                CartFragmentDirections.actionCartFragmentToOrderDetailsFragment(
                    orderResponse, getString(R.string.title_cart)
                )
            )
        }
    }

    override fun onSlotClick(time: String) {
        customerNote = time
        val paymentMethodBottomSheetFragment =
            PaymentMethodFragment(
                this,
                totalAmount.toString(),
                getString(R.string.title_cart)
            )
        paymentMethodBottomSheetFragment.show(
            requireActivity().supportFragmentManager,
            paymentMethodBottomSheetFragment.tag
        )
    }

    override fun onCouponItemClick(coupon: DatabaseCoupon) {
        val dateExpire = coupon.dateExpiresGmt
        val today = getCurrentDate("YYYY-MM-dd'T'HH:mm:ss")
        if (checkDates(dateExpire, today)) {
            if (selectedCouponList.isEmpty()) {
                selectedCouponList.add(coupon)
                val discountType = coupon.discountType
                val amount = coupon.amount
                val minimum = coupon.minimumAmount

                coupon.product_categories?.let {
                    Timber.e("Coupons product category list size : ${it.size}")
                    isCategoryCoupon = it.isNotEmpty()
                } ?: let {
                    Timber.e("Coupons product category list is empty")
                    isCategoryCoupon = false
                }

                if (!isCategoryCoupon) {
                    Timber.e("It is not a category coupon")
                    when {
                        minimum.toDouble() > totalAmount -> {
                            Toast.makeText(
                                requireContext(),
                                "Minimum Purchase Amount Should be $minimum to use this Coupon",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        else -> {
                            if (discountType == getString(R.string.fixed_cart)) {
                                totalAmount = totalAmount.minus(amount.toDouble())
                                couponMoney = amount.toDouble()
                                viewModel.setTotal(totalAmount.toString())
                            } else if (discountType == "percent") {
                                val discount =
                                    amount.toDouble().div(100).times(totalAmount).roundToInt()
                                couponMoney = discount.toDouble()
                                totalAmount = totalAmount.minus(discount)
                                viewModel.setTotal(totalAmount.toString())
                            }
                            currentCoupon = coupon
                            Timber.e("Current coupon code: ${currentCoupon!!.code}")
                            coupons.add(CouponLine(currentCoupon!!.code))
                            Timber.e("Coupon list size: ${coupons.size}")
                        }
                    }

                } else {
                    Timber.e("It is a category coupon")
                    val (match, rest) = cartList.toList().partition {
                        it.catId == coupon.product_categories?.get(0) || it.subCatId == coupon.product_categories?.get(
                            0
                        )
                    }
                    Timber.e("Match list size: ${match.size}")

                    if (match.isNotEmpty() && match.size>1) {
                        var matchItemsAmount = 0.0
                        for (item in match) {
                            Timber.e("match item sale price: ${item.salePrice}, quantity: ${item.quantity}")
                            matchItemsAmount =
                                matchItemsAmount.plus(
                                    item.salePrice!!.toDouble().times(item.quantity)
                                )
                            Timber.e("Match item amount: $matchItemsAmount")
                        }
                        Timber.e("Total match amount: $matchItemsAmount")
                        when {
                            matchItemsAmount != 0.0 && matchItemsAmount > minimum.toDouble() -> {
                                if (discountType == getString(R.string.fixed_cart)) {
                                    totalAmount = totalAmount.minus(amount.toDouble())
                                    couponMoney = amount.toDouble()
                                    viewModel.setTotal(totalAmount.toString())
                                } else if (discountType == "percent") {
                                    val discount =
                                        amount.toDouble().div(100).times(totalAmount).roundToInt()
                                    couponMoney = discount.toDouble()
                                    totalAmount = totalAmount.minus(discount)
                                    viewModel.setTotal(totalAmount.toString())
                                }
                                currentCoupon = coupon
                                Timber.e("Current coupon code: ${currentCoupon!!.code}")
                                coupons.add(CouponLine(currentCoupon!!.code))
//                                for(item in selectedCouponList){
//                                    coupons.add(CouponLine(item.code))
//                                }
                                Timber.e("Coupon list size: ${coupons.size}")
                            }
                            else -> {
                                Toast.makeText(
                                    requireContext(),
                                    "Minimum Purchase Amount Should be $minimum to use this Coupon",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                    else if(match.size <= 1){
                        Toast.makeText(requireContext(),"You should have more than one item of the Category to apply Coupon",
                        Toast.LENGTH_LONG).show()
                    }
                    else {
                        Toast.makeText(
                            requireContext(),
                            "This coupon Cannot be applied to Products in Your Cart",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else if (selectedCouponList.isNotEmpty() && !selectedCouponList.contains(coupon)) {
                selectedCouponList.removeAt(0)
                selectedCouponList.clear()
                selectedCouponList.add(coupon)
                Timber.e("Total amount: $totalAmount")
                totalAmount = totalAmount.plus(couponMoney)
                Timber.e("Total amount: $totalAmount")
                viewModel.setTotal(totalAmount.toString()).let {
                    Timber.e("Total amount: $totalAmount")
                    val discountType = coupon.discountType
                    val amount = coupon.amount
                    val minimum = coupon.minimumAmount

                    coupon.product_categories?.let {
                        Timber.e("Coupons product category list size : ${it.size}")
                        isCategoryCoupon = it.isNotEmpty()
                    } ?: let {
                        Timber.e("Coupons product category list is empty")
                        isCategoryCoupon = false
                    }

                    if (!isCategoryCoupon) {
                        Timber.e("It is not a category coupon")
                        if (minimum.toDouble() > totalAmount) {
                            Toast.makeText(
                                requireContext(),
                                "Minimum Purchase Amount Should be $minimum to use this Coupon",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            if (discountType == getString(R.string.fixed_cart)) {
                                Timber.e("Total amount: $totalAmount")
                                totalAmount = totalAmount.minus(amount.toDouble())
                                couponMoney = amount.toDouble()
                                viewModel.setTotal(totalAmount.toString())
                                Timber.e("Total amount: $totalAmount")
                            } else if (discountType == "percent") {
                                Timber.e("Total amount: $totalAmount")
                                val discount =
                                    amount.toDouble().div(100).times(totalAmount).roundToInt()
                                couponMoney = discount.toDouble()
                                totalAmount = totalAmount.minus(discount)
                                viewModel.setTotal(totalAmount.toString())
                                Timber.e("Total amount: $totalAmount")
                            }
                            currentCoupon = coupon
                            coupons.add(CouponLine(currentCoupon!!.code))
                        }

                    } else {
                        Timber.e("It is a category coupon")
                        val (match, rest) = cartList.toList().partition {
                            it.catId == coupon.product_categories?.get(0) || it.subCatId == coupon.product_categories?.get(
                                0
                            )
                        }
                        Timber.e("Match list size: ${match.size}")

                        if (match.isNotEmpty()&& match.size>1) {
                            var matchItemsAmount = 0.0
                            for (item in match) {
                                Timber.e("match item sale price: ${item.salePrice}, quantity: ${item.quantity}")
                                matchItemsAmount =
                                    matchItemsAmount.plus(
                                        item.salePrice!!.toDouble().times(item.quantity)
                                    )
                                Timber.e("Match item amount: $matchItemsAmount")
                            }
                            Timber.e("Total match amount: $matchItemsAmount")
                            when {
                                matchItemsAmount != 0.0 && matchItemsAmount > minimum.toDouble() -> {
                                    if (discountType == getString(R.string.fixed_cart)) {
                                        totalAmount = totalAmount.minus(amount.toDouble())
                                        couponMoney = amount.toDouble()
                                        viewModel.setTotal(totalAmount.toString())
                                    } else if (discountType == "percent") {
                                        val discount =
                                            amount.toDouble().div(100).times(totalAmount)
                                                .roundToInt()
                                        couponMoney = discount.toDouble()
                                        totalAmount = totalAmount.minus(discount)

                                        viewModel.setTotal(totalAmount.toString())
                                    }
                                    currentCoupon = coupon
                                    Timber.e("Current coupon code: ${currentCoupon!!.code}")
                                    coupons.add(CouponLine(currentCoupon!!.code))
                                    Timber.e("Coupon list size: ${coupons.size}")
                                }
                                else -> {
                                    Toast.makeText(
                                        requireContext(),
                                        "Minimum Purchase Amount Should be $minimum to use this Coupon",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                        else if(match.size <= 1){
                            Toast.makeText(requireContext(),"You should have more than one item of the Category to apply Coupon",
                                Toast.LENGTH_LONG).show()
                        }
                        else {
                            Toast.makeText(
                                requireContext(),
                                "This coupon Cannot be applied to Products in Your Cart",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "This Coupon is Already Selected",
                    Toast.LENGTH_LONG
                )
                    .show()
            }

        } else {
            Toast.makeText(requireContext(), "This Coupon has expired", Toast.LENGTH_LONG).show()
            viewModel.removeCoupon(coupon)
        }
    }

    private fun checkDates(d1: String, d2: String): Boolean {
        Timber.e("coupon date: $d1, today: $d2")
        val dfDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        var b = false
        try {
            b = when {
                dfDate.parse(d1)!!.after(dfDate.parse(d2)) -> {
                    true;//If start date is before end date
                }
                dfDate.parse(d1)!! == dfDate.parse(d2) -> {
                    true
                }
                else -> {
                    false
                }
            }
        } catch (e: ParseException) {
            e.printStackTrace()
            Timber.e("Failed to compare dates: ${e.message}")
        }
        return b
    }

    private fun createLoadingDialog(): AlertDialog {
        val layout = LayoutInflater.from(requireContext()).inflate(R.layout.loading_layout, null)
        val builder = AlertDialog.Builder(requireContext(), R.style.TransparentDialog)
        builder.setView(layout)
        builder.setCancelable(false)
        return builder.create()
    }

    override fun onCartListItemClick(cart: DatabaseCart) {
        selectedCouponList.clear()
        Toast.makeText(requireContext(), "Coupon that applied was Removed", Toast.LENGTH_SHORT)
            .show()
    }
}