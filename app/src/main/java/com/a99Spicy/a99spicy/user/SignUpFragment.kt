package com.a99Spicy.a99spicy.user

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.a99Spicy.a99spicy.databinding.FragmentSignUpBinding
import com.a99Spicy.a99spicy.utils.AppUtils
import com.google.android.material.button.MaterialButton
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.mukesh.OtpView
import timber.log.Timber
import java.util.concurrent.TimeUnit

class SignUpFragment : Fragment() {

    private lateinit var signUpFragmentBinding: FragmentSignUpBinding

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mCallBacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var mVerificationId: String
    private lateinit var mResendToken: PhoneAuthProvider.ForceResendingToken

    private lateinit var otp: String
    private lateinit var phoneNumber: String
    private lateinit var name: String
    private lateinit var userName: String
    private var fToken: String = ""
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
        signUpFragmentBinding = FragmentSignUpBinding.inflate(inflater, container, false)

        //Initializing ViewModel class
        viewModel = ViewModelProvider(this).get(SplashViewModel::class.java)

        //Initializing FireBase Auth
        mAuth = FirebaseAuth.getInstance()

        //Adding callback to read otp and begin sign up with phone to firebase
        mCallBacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Timber.e("onVerificationCompleted:$credential")
                otpDialog.dismiss()
                signInWithPhoneAuthCredential(credential)
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
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Timber.e("onCodeSent:$verificationId")

                // Save verification ID and resending token so we can use them later
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

        //Add textChangeListener to texinput layouts
        signUpFragmentBinding.signUpNameTextinputEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                signUpFragmentBinding.signUpNameTextinputlayuot.isErrorEnabled = false
            }

            override fun afterTextChanged(s: Editable?) {
                signUpFragmentBinding.signUpNameTextinputlayuot.isErrorEnabled = true
            }
        })

        signUpFragmentBinding.signUpPhoneTextinputEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                signUpFragmentBinding.signUpPhoneTextinputlayuot.isErrorEnabled = false
            }

            override fun afterTextChanged(s: Editable?) {
                signUpFragmentBinding.signUpPhoneTextinputlayuot.isErrorEnabled = true
            }
        })

        //Set onClickListener to verify button
        signUpFragmentBinding.signUpVerifyButton.setOnClickListener {

            name = signUpFragmentBinding.signUpNameTextinputEt.text.toString()
            phoneNumber = signUpFragmentBinding.signUpPhoneTextinputEt.text.toString()

            when {
                name.isEmpty() -> {
                    signUpFragmentBinding.signUpNameTextinputlayuot.error = "Enter Full Name"
                }
                phoneNumber.isEmpty() -> {
                    signUpFragmentBinding.signUpPhoneTextinputlayuot.error =
                        "Enter Valid Phone number"
                }
                else -> {
                    sendOtp("+91$phoneNumber")
                }
            }

        }

        //Set onClickListener to create account button
        signUpFragmentBinding.createAccountButton.setOnClickListener {

            loadingDialog = createLoadingDialog()
            loadingDialog.show()
            viewModel.createUser(
                name,
                "+91",
                phoneNumber,
                AppUtils.createUserName(name),
                fToken
            )
        }

        //Observe create user live data
        viewModel.createUserLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {

                Timber.e("user id: ${it.data.userId}")
                Toast.makeText(requireContext(), "Account Created Successfully", Toast.LENGTH_SHORT)
                    .show()
                findNavController().navigate(
                    SignUpFragmentDirections.actionSignUpFragmentToLocationFragment(
                        it.data.userId, name, phoneNumber
                    )
                )
            }
        })

        //Observe Loading value from ViewModel
        viewModel.signUpLoadingLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it == SignUpLoading.SIGNUP_FAILED) {
                    loadingDialog.dismiss()
                    Toast.makeText(
                        requireContext(),
                        "Failed to Create Account, Try again",
                        Toast.LENGTH_LONG
                    ).show()
                }else if (it == SignUpLoading.SIGNUP_SUCCESS){
                    loadingDialog.dismiss()
                }
            }
        })

        return signUpFragmentBinding.root
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

        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Timber.e("signInWithCredential:success")
                    val user = task.result?.user
                    user?.let {
                        getFirebaseToken(it)
                        Toast.makeText(
                            requireContext(),
                            "Phone number verified",
                            Toast.LENGTH_SHORT
                        ).show()
                        signUpFragmentBinding.createAccountButton.isEnabled = true
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
                        fToken = it
                        // Send token to your backend via HTTPS

                        Timber.e("Firebase token: $fToken")
                    }
                    // ...
                } else {
                    // Handle error -> task.getException();
                    Timber.e("Failed to get user token: ${task.exception.toString()}")
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

    private fun createLoadingDialog(): AlertDialog {
        val layout = LayoutInflater.from(requireContext()).inflate(R.layout.loading_layout, null)
        val builder = AlertDialog.Builder(requireContext(), R.style.TransparentDialog)
        builder.setView(layout)
        builder.setCancelable(false)
        return builder.create()
    }
}