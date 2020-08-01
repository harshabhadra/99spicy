package com.a99Spicy.a99spicy.user

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.a99Spicy.a99spicy.R
import com.a99Spicy.a99spicy.databinding.FragmentLoginBinding
import com.a99Spicy.a99spicy.ui.HomeActivity

class LoginFragment : Fragment() {

    private lateinit var loginFragmentLoginBinding: FragmentLoginBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        loginFragmentLoginBinding = FragmentLoginBinding.inflate(inflater,container,false)

        //Set onClickListener to generate otp button
        loginFragmentLoginBinding.logInGenerateOtpButton.setOnClickListener {
            val intent = Intent(requireActivity(),HomeActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
        return loginFragmentLoginBinding.root
    }
}