package com.a99Spicy.a99spicy.ui.payment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.a99Spicy.a99spicy.R
import com.a99Spicy.a99spicy.network.PlaceOrder
import com.a99Spicy.a99spicy.network.RazorPayPayment
import com.a99Spicy.a99spicy.utils.AppUtils
import com.a99Spicy.a99spicy.utils.Constants
import com.paytm.pgsdk.PaytmOrder
import com.paytm.pgsdk.PaytmPaymentTransactionCallback
import com.paytm.pgsdk.TransactionManager
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject
import timber.log.Timber

class PaymentActivity : AppCompatActivity(), PaymentResultListener {

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
    private lateinit var pType: String

    private lateinit var viewModel: PaymentViewModel
    private lateinit var loadingDialog: AlertDialog
    private lateinit var order: PlaceOrder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        //Initializing ViewModel class
        viewModel = ViewModelProvider(this).get(PaymentViewModel::class.java)

        val intent = intent
        intent?.let {
            argAmount = intent.getStringExtra(Constants.AMOUNT)!!
            pMethod = intent.getStringExtra(Constants.TRANSACTION_MODE)!!
            pType = intent.getStringExtra(Constants.TRANSACTION_TYPE)!!
            if (pType == getString(R.string.place_order)) {
                order = intent.getParcelableExtra(Constants.ORDER)!!
            }
        }

        loadingDialog = createLoadingDialog()
        loadingDialog.show()

        when (pMethod) {
            getString(R.string.credit_card_debit_card_upi) -> {
                Checkout.preload(this)
                //Generate Razor pay order id
                val razorPayPayment = RazorPayPayment("100", "INR", 1)
                viewModel.generateOrderId(razorPayPayment)
            }
            else -> {
                //Generate transaction token for payTm
                payTmOrderId = AppUtils.generatePaytmOrderId()
                viewModel.generateTxnToken(payTmOrderId, "CUST_001", 1.00)
            }
        }

        //Observe order id for razor pay
        viewModel.razorPayLiveData.observe(this, Observer {
            it?.let {
                orderId = it.id
                loadingDialog.dismiss()
                startPayment()
            }
        })

        //Observe txnToken for paytm
        viewModel.payTmTxnTokenLiveData.observe(this, Observer {
            it?.let {
                loadingDialog.dismiss()
                txnToken = it.body.txnToken
                payCallBackUrl ="https://securegw.paytm.in/theia/paytmCallback?ORDER_ID=$payTmOrderId"
               Timber.e("Call back url: $payCallBackUrl")

                paytmOrder = PaytmOrder(payTmOrderId, "qEOCXH55386187334129", txnToken, "1.00", payCallBackUrl)
                val transactionManager =
                    TransactionManager(paytmOrder, object : PaytmPaymentTransactionCallback {
                        override fun onTransactionResponse(p0: Bundle?) {
                            Toast.makeText(
                                applicationContext,
                                "Payment Transaction response " + p0.toString(),
                                Toast.LENGTH_LONG
                            ).show()
                            Timber.e("Payment response: ${p0.toString()}")
                            val pIntent = Intent()
                            pIntent.putExtra("message", "Success")
                            setResult(1002, pIntent)
                            finish()
                        }

                        override fun clientAuthenticationFailed(p0: String?) {
                            Toast.makeText(
                                applicationContext,
                                "Payment Transaction response " + p0.toString(),
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        override fun someUIErrorOccurred(p0: String?) {
                            Toast.makeText(
                                applicationContext,
                                "Payment Transaction response " + p0.toString(),
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        override fun onTransactionCancel(p0: String?, p1: Bundle?) {
                            Toast.makeText(
                                applicationContext,
                                "Payment Transaction response " + p0.toString(),
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        override fun networkNotAvailable() {
                            Toast.makeText(
                                applicationContext,
                                "Network not available, Check your internet",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        override fun onErrorProceed(p0: String?) {
                            Toast.makeText(
                                applicationContext,
                                "Payment Transaction response " + p0.toString(),
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        override fun onErrorLoadingWebPage(p0: Int, p1: String?, p2: String?) {
                            Toast.makeText(
                                applicationContext,
                                "Payment Transaction response $p0",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        override fun onBackPressedCancelTransaction() {

                        }
                    })
                transactionManager.startTransaction(this, 12345)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 12345 && data != null) {
            Toast.makeText(
                applicationContext,
                data.getStringExtra("nativeSdkForMerchantMessage") + data.getStringExtra("response"),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun startPayment() {
        /*
        *  You need to pass current activity in order to let Razorpay create CheckoutActivity
        * */
        val co = Checkout()
        co.setImage(R.drawable.app_logo)
        co.setFullScreenDisable(true)
        try {
            val options = JSONObject()
            options.put("name", getString(R.string.app_name))
            options.put("theme.color", "#3f51b5");
            options.put("currency", "INR");
            options.put("order_id", orderId);
            options.put("amount", "100")//pass amount in currency subunits
            co.open(this, options)
        } catch (e: Exception) {
            Toast.makeText(applicationContext, "Error in payment: " + e.message, Toast.LENGTH_LONG)
                .show()
            Timber.e("Error ${e.message}")
        }
    }

    private fun createPaytmOrder(): PaytmOrder {
        return PaytmOrder(payTmOrderId, "qEOCXH55386187334129", txnToken, "1.00", payCallBackUrl)
    }

    private fun createLoadingDialog(): AlertDialog {
        val layout = LayoutInflater.from(this).inflate(R.layout.loading_layout, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(layout)
        builder.setCancelable(false)
        return builder.create()
    }


    override fun onPaymentSuccess(p0: String?) {

        if (pType == getString(R.string.place_order)) {
            val intent = Intent()
            intent.putExtra(Constants.MESSAGE, getString(R.string.success))
            setResult(Constants.PLACE_ORDER_REQUEST_CODE, intent)
            finish()
        } else {
            val intent = Intent()
            intent.putExtra(Constants.MESSAGE, getString(R.string.success))
            intent.putExtra(Constants.AMOUNT, argAmount.toDouble())
            setResult(Constants.ADD_TO_WALLET_REQUEST_CODE, intent)
            finish()
        }
    }

    override fun onPaymentError(p0: Int, p1: String?) {

        if (pType == getString(R.string.place_order)) {
            val intent = Intent()
            intent.putExtra(Constants.MESSAGE, getString(R.string.failed))
            setResult(Constants.PLACE_ORDER_REQUEST_CODE, intent)
            finish()
        } else {
            val intent = Intent()
            intent.putExtra(Constants.MESSAGE, getString(R.string.failed))
            intent.putExtra(Constants.AMOUNT, argAmount.toDouble())
            setResult(Constants.ADD_TO_WALLET_REQUEST_CODE, intent)
            finish()
        }
    }
}