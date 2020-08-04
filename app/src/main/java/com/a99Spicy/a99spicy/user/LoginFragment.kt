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
import com.a99Spicy.a99spicy.R
import com.a99Spicy.a99spicy.databinding.FragmentLoginBinding
import com.a99Spicy.a99spicy.ui.HomeActivity
import com.a99Spicy.a99spicy.utils.Constants
import com.google.android.material.button.MaterialButton
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
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

    private lateinit var otpDialog: AlertDialog
    private lateinit var otpView: OtpView
    private lateinit var verifyOtpButton: MaterialButton
    private lateinit var otpProgressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        loginFragmentLoginBinding = FragmentLoginBinding.inflate(inflater, container, false)

        //Initializing FireBase Auth
        mAuth = FirebaseAuth.getInstance()

        //Adding callback to read otp and begin sign up with phone to firebase
        mCallBacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
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

        //Set onClickListener to generate otp button
        loginFragmentLoginBinding.logInGenerateOtpButton.setOnClickListener {

            phoneNumber = loginFragmentLoginBinding.logInPhoneTextInpu.text.toString()
            sendOtp(phoneNumber)
        }
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

        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Timber.e("signInWithCredential:success")
                    val user = task.result?.user
                    user?.let {
                        Toast.makeText(
                            requireContext(),
                            "Sign in successfully: ${it.phoneNumber}",
                            Toast.LENGTH_SHORT
                        ).show()
                        val sharedPreferences = requireActivity().getSharedPreferences(Constants.LOG_IN,Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putBoolean(Constants.IS_LOG_IN,true)
                        editor.apply()
                        goToHome()
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

    private fun goToHome() {
        val intent = Intent(requireActivity(), HomeActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }
}