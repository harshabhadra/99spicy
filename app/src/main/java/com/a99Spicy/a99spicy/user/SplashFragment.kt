package com.a99Spicy.a99spicy.user

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.a99Spicy.a99spicy.R
import com.a99Spicy.a99spicy.intro.IntroActivity
import com.a99Spicy.a99spicy.ui.HomeActivity
import com.a99Spicy.a99spicy.utils.Constants

private var isLogIn = false
private var userId:String? = null
private lateinit var sharedPreferences: SharedPreferences

class SplashFragment : Fragment() {

    private lateinit var viewModel: SplashViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.splash_fragment, container, false)

        sharedPreferences =
            requireActivity().getSharedPreferences(Constants.LOG_IN, Context.MODE_PRIVATE)
        val isFirst = sharedPreferences.getBoolean(Constants.IS_FIRST,true)
        isLogIn = sharedPreferences.getBoolean(Constants.IS_LOG_IN, false)
        userId = sharedPreferences.getString(Constants.SAVED_USER_ID,null)

        Handler().postDelayed({
            if (!isFirst) {
                if (isLogIn && !(userId.isNullOrEmpty())) {
                    goToHome(userId!!)
                } else {
                    findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToLoginFragment())
                }
            }else{
                val intent = Intent(requireActivity(), IntroActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
        }, 3000)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SplashViewModel::class.java)
        // TODO: Use the ViewModel
    }

    private fun goToHome(userId:String) {

        val intent = Intent(requireActivity(), HomeActivity::class.java)
        intent.putExtra(Constants.USER_ID, userId)
        startActivity(intent)
        requireActivity().finish()
    }
}