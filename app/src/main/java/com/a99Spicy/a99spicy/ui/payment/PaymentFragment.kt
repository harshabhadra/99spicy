package com.a99Spicy.a99spicy.ui.payment

import android.content.Intent
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
import com.a99Spicy.a99spicy.R
import com.a99Spicy.a99spicy.network.PlaceOrder
import com.a99Spicy.a99spicy.network.RazorPayPayment
import com.a99Spicy.a99spicy.ui.HomeActivity
import com.a99Spicy.a99spicy.utils.AppUtils
import com.paytm.pgsdk.PaytmOrder
import com.paytm.pgsdk.PaytmPaymentTransactionCallback
import com.paytm.pgsdk.TransactionManager
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultListener
import com.razorpay.PaymentResultWithDataListener
import org.json.JSONObject
import timber.log.Timber

class PaymentFragment : Fragment(), PaymentResultWithDataListener {

    private lateinit var orderId: String
    private lateinit var payTmOrderId: String
    private var payTmCallBackUrl: String =
        "https://securegw.paytm.in/"
    private lateinit var payCallBackUrl: String
    private val Mid: String = "BPnato79059276154686"
    private lateinit var txnToken: String
    private lateinit var paytmOrder: PaytmOrder

    private lateinit var argAmount: String
    private lateinit var pMethod: String

    private lateinit var viewModel: PaymentViewModel
    private lateinit var loadingDialog: AlertDialog
    private lateinit var order: PlaceOrder

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //Inflating layout
        val view = inflater.inflate(R.layout.payment_fragment, container, false)

        //Initializing ViewModel class
        viewModel = ViewModelProvider(this).get(PaymentViewModel::class.java)


        val activity = activity as HomeActivity
        activity.setAppBarElevation(0F)
        activity.setToolbarTitle("Payment")
        activity.setToolbarLogo(null)

        loadingDialog = createLoadingDialog()
        loadingDialog.show()

        when (pMethod) {
            getString(R.string.credit_card_debit_card_upi) -> {
                Checkout.preload(requireContext())
                //Generate Razor pay order id
                val razorPayPayment = RazorPayPayment("100", "INR", 1)
                viewModel.generateOrderId(razorPayPayment)
            }
            else -> {
                payTmOrderId = AppUtils.generatePaytmOrderId()
                payCallBackUrl = payTmCallBackUrl + "theia/paytmCallback?ORDER_ID=" + payTmOrderId
                //Generate transaction token for payTm
                viewModel.generateTxnToken(payTmOrderId, "CUST_001", 1.00)
            }
        }

        //Observe order id for razor pay
        viewModel.razorPayLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                orderId = it.id
                loadingDialog.dismiss()
                startPayment()
            }
        })

        //Observe txnToken for paytm
        viewModel.payTmTxnTokenLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                loadingDialog.dismiss()
                txnToken = it.body.txnToken
                paytmOrder = createPaytmOrder()
                val transactionManager =
                    TransactionManager(paytmOrder, object : PaytmPaymentTransactionCallback {
                        override fun onTransactionResponse(p0: Bundle?) {
                            Toast.makeText(
                                requireContext(),
                                "Payment Transaction response " + p0.toString(),
                                Toast.LENGTH_LONG
                            ).show()
                            Timber.e("Payment response: ${p0.toString()}")
                        }

                        override fun clientAuthenticationFailed(p0: String?) {
                            Toast.makeText(
                                requireContext(),
                                "Payment Transaction response " + p0.toString(),
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        override fun someUIErrorOccurred(p0: String?) {
                            Toast.makeText(
                                requireContext(),
                                "Payment Transaction response " + p0.toString(),
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        override fun onTransactionCancel(p0: String?, p1: Bundle?) {
                            Toast.makeText(
                                requireContext(),
                                "Payment Transaction response " + p0.toString(),
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        override fun networkNotAvailable() {
                            Toast.makeText(
                                requireContext(),
                                "Network not available, Check your internet",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        override fun onErrorProceed(p0: String?) {
                            Toast.makeText(
                                requireContext(),
                                "Payment Transaction response " + p0.toString(),
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        override fun onErrorLoadingWebPage(p0: Int, p1: String?, p2: String?) {
                            Toast.makeText(
                                requireContext(),
                                "Payment Transaction response $p0",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        override fun onBackPressedCancelTransaction() {

                        }
                    })
                transactionManager.startTransaction(requireActivity(), 12345)
            }
        })

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 12345 && data != null) {
            Toast.makeText(
                requireContext(),
                data.getStringExtra("nativeSdkForMerchantMessage") + data.getStringExtra("response"),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun createPaytmOrder(): PaytmOrder {
        return PaytmOrder(payTmOrderId, "qEOCXH55386187334129", txnToken, "1.00", payCallBackUrl)
    }

    private fun startPayment() {
        /*
        *  You need to pass current activity in order to let Razorpay create CheckoutActivity
        * */
        val co = Checkout()
        co.setKeyID("rzp_live_oX0LM5QcdnNfXj")
        co.setImage(R.drawable.app_logo)
        co.setFullScreenDisable(true)
        try {
            val options = JSONObject()
            options.put("name", getString(R.string.app_name))
            options.put("theme.color", "#3f51b5");
            options.put("currency", "INR");
            options.put("order_id", orderId);
            options.put("amount", "100")//pass amount in currency subunits
            co.open(requireActivity(), options)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error in payment: " + e.message, Toast.LENGTH_LONG).show()
            Timber.e("Error ${e.message}")
        }
    }


    private fun createLoadingDialog(): AlertDialog {
        val layout = LayoutInflater.from(requireContext()).inflate(R.layout.loading_layout, null)
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(layout)
        builder.setCancelable(false)
        return builder.create()
    }

    override fun onPaymentSuccess(p0: String?, p1: PaymentData?) {
        Toast.makeText(requireContext(), "Payment Successful: $p0", Toast.LENGTH_LONG).show()
        Timber.e("Payment success response: $p0")
    }

    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
        Toast.makeText(requireContext(), "Payment failed: $p1", Toast.LENGTH_LONG).show()
        Timber.e("Payment failed response: $p0")
    }
}