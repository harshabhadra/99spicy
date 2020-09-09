package com.a99Spicy.a99spicy.user

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.a99Spicy.a99spicy.R
import com.a99Spicy.a99spicy.databinding.FragmentLoginBinding
import com.a99Spicy.a99spicy.network.Profile
import com.a99Spicy.a99spicy.ui.HomeActivity
import com.a99Spicy.a99spicy.utils.Constants
import com.google.android.material.button.MaterialButton
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.mukesh.OtpView
import timber.log.Timber
import java.util.concurrent.TimeUnit


class LoginFragment : Fragment() {

    private lateinit var loginFragmentLoginBinding: FragmentLoginBinding

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mCallBacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var mVerificationId: String
    private lateinit var mResendToken: PhoneAuthProvider.ForceResendingToken

    private lateinit var otp: String
    private lateinit var phoneNumber: String
    private var userId: String = ""

    private lateinit var otpDialog: AlertDialog
    private lateinit var otpView: OtpView
    private lateinit var verifyOtpButton: MaterialButton
    private lateinit var otpProgressBar: ProgressBar
    private lateinit var loadingDialog: AlertDialog

    private lateinit var viewModel: SplashViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        loginFragmentLoginBinding = FragmentLoginBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(this).get(SplashViewModel::class.java)

        //Initializing FireBase Auth
        mAuth = FirebaseAuth.getInstance()

        //Adding callback to read otp and begin sign up with phone to firebase
        mCallBacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Timber.e("onVerificationCompleted:$credential")
//                otpDialog.dismiss()
//                signInWithPhoneAuthCredential(credential)
                otpView.isEnabled = true
                verifyOtpButton.isEnabled = true
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Timber.e("onVerificationFailed ${e.message}")

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                    Timber.e("Invalid request: ${e.message}")
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                    Timber.e("Sms quota for app has been exceeded: ${e.message}")
                }

                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                // Show a message and update the UI
                // ...
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                mVerificationId = verificationId
                mResendToken = token

                otpDialog = createOtpDialog()
                otpDialog.show()
                // ...
            }

            override fun onCodeAutoRetrievalTimeOut(p0: String) {
                super.onCodeAutoRetrievalTimeOut(p0)

                Toast.makeText(requireContext(), "Enter OTP and Verify", Toast.LENGTH_SHORT).show()
                otpView.isEnabled = true
            }
        }

        //Set onClickListener to generate otp button
        loginFragmentLoginBinding.logInGenerateOtpButton.setOnClickListener {
            phoneNumber = loginFragmentLoginBinding.logInPhoneTextInpu.text.toString()
            sendOtp("+91${phoneNumber}")
        }

        //Set onClickListener to create User button
        loginFragmentLoginBinding.logInCreateAccountButton.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToSignUpFragment())
        }

        //Observe fTokenLiveData
        viewModel.fTokenLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                viewModel.loginUser(phoneNumber, "+91", it, otp)
                viewModel.resetFToken()
            }
        })

        //observe loginLiveData
        viewModel.loginLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                userId = it.data.userId
                Toast.makeText(requireContext(), "Log in Successful", Toast.LENGTH_SHORT).show()
                val sharedPreferences = requireActivity().getSharedPreferences(
                    Constants.LOG_IN,
                    Context.MODE_PRIVATE
                )
                val editor = sharedPreferences.edit()
                editor.putBoolean(Constants.IS_LOG_IN, true)
                editor.putString(Constants.SAVED_USER_ID, userId)
                editor.putString(Constants.PHONE, phoneNumber)
                editor.apply()
                goToHome(userId, null)
            }
        })

        //Observe loading value from ViewModel
        viewModel.loginLoadingLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it == LoginLoading.LOGIN_SUCCESS) {
                    loadingDialog.dismiss()
                } else if (it == LoginLoading.LOGIN_FAILED) {
                    loadingDialog.dismiss()
                    Toast.makeText(
                        requireContext(),
                        "Failed to Log in, Try again",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })
        return loginFragmentLoginBinding.root
    }

    private fun sendOtp(phoneNumber: String) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber,  // Phone number to verify
            30,  // Timeout duration
            TimeUnit.SECONDS,  // Unit of timeout
            requireActivity(),  // Activity (for callback binding)
            mCallBacks
        ) // OnVerificationStateChangedCallbacks
    }

    //Sign in with phone number to firebase
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {

        loadingDialog = createLoadingDialog()
        loadingDialog.show()

        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Timber.e("signInWithCredential:success")
                    val user = task.result?.user
                    user?.let {
                        getFirebaseToken(it)
                    }
                } else {
                    // Sign in failed, display a message and update the UI
                    Timber.e("signInWithCredential:failure ${task.exception}")
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Toast.makeText(
                            requireContext(),
                            "The verification code entered was invalid",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
    }

    private fun getFirebaseToken(mUser: FirebaseUser) {

        mUser.getIdToken(true)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val idToken = task.result!!.token
                    idToken?.let {
                        viewModel.setFToken(it)
                    }
                    // Send token to your backend via HTTPS
                    // ...
                } else {
                    // Handle error -> task.getException();
                    loadingDialog.dismiss()
                    Toast.makeText(requireContext(), task.exception.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    //Create opt dialog
    private fun createOtpDialog(): AlertDialog {
        val layout = LayoutInflater.from(requireContext()).inflate(R.layout.otp_layout, null)
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(layout)

        otpView = layout.findViewById(R.id.otp_view)
        verifyOtpButton = layout.findViewById(R.id.otp_verify_button)
        otpProgressBar = layout.findViewById(R.id.otp_progressBar)

        val dialog = builder.create()

        //Set listener to otpView
        otpView.setOtpCompletionListener {
            it?.let {
                otp = it
                verifyOtpButton.isEnabled = true
            }
        }

        //Set onClickListener to verify button
        verifyOtpButton.setOnClickListener {
            val credential = PhoneAuthProvider.getCredential(mVerificationId, otp)
            signInWithPhoneAuthCredential(credential)
            dialog.dismiss()
        }

        return dialog
    }

    private fun goToHome(userId: String, profile: Profile?) {
        val intent = Intent(requireActivity(), HomeActivity::class.java)
        intent.putExtra(Constants.USER_ID, userId)
        intent.putExtra(Constants.PROFILE, profile)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun createLoadingDialog(): AlertDialog {
        val layout = LayoutInflater.from(requireContext()).inflate(R.layout.loading_layout, null)
        val builder = AlertDialog.Builder(requireContext(), R.style.TransparentDialog)
        builder.setView(layout)
        builder.setCancelable(false)
        return builder.create()
    }
}