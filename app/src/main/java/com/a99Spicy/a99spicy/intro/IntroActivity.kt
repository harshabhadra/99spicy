package com.a99Spicy.a99spicy.intro

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.a99Spicy.a99spicy.MainActivity
import com.a99Spicy.a99spicy.R
import com.a99Spicy.a99spicy.ui.HomeActivity
import com.a99Spicy.a99spicy.utils.Constants
import com.github.appintro.AppIntro2
import com.github.appintro.AppIntroFragment
import com.github.appintro.AppIntroPageTransformerType

private lateinit var sharedPreferences: SharedPreferences
class IntroActivity : AppIntro2() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences(Constants.LOG_IN, Context.MODE_PRIVATE)
        addSlide(
            AppIntroFragment.newInstance(
                getString(R.string.app_name),
                "Welcome to the Best online Grocery Store in India. Buy essentials from your comfort.",
                R.drawable.intro_e,
                Color.WHITE,
                Color.BLACK,
                Color.BLACK,
                R.font.open_sans_bold,
                R.font.open_sans_bold
            )
        )

        addSlide(
            AppIntroFragment.newInstance(
                "Quick Delivery",
                "Order anything, we will deliver whatever you need to your doorstep",
                R.drawable.delivery_intro_b,
                Color.WHITE,
                Color.BLACK,
                Color.BLACK,
                R.font.open_sans_bold,
                R.font.open_sans_bold
            )
        )

        addSlide(
            AppIntroFragment.newInstance(
                getString(R.string.subscribe_products),
                getString(R.string.subscribe_intro_description),
                R.drawable.intro_d,
                Color.WHITE,
                Color.BLACK,
                Color.BLACK,
                R.font.open_sans_bold,
                R.font.open_sans_bold
            )
        )

        addSlide(
            AppIntroFragment.newInstance(
                "Location Permission",
                "Location Permission Required to show products available in your city and deliver to your doorstep",
                R.drawable.delivery_intro,
                Color.WHITE,
                Color.BLACK,
                Color.BLACK,
                R.font.open_sans_bold,
                R.font.open_sans_bold
            )
        )
        setTransformer(AppIntroPageTransformerType.Flow)
        isColorTransitionsEnabled = true
        // Toggle Indicator Visibility
        isIndicatorEnabled = true
        isSystemBackButtonLocked = true
        isWizardMode = true
        setIndicatorColor(
            selectedIndicatorColor = R.color.colorPrimaryDark,
            unselectedIndicatorColor = R.color.colorPrimaryLight
        )
//        setImmersiveMode()
        askForPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 4, false)
    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        val editor = sharedPreferences.edit()
        editor.putBoolean(Constants.IS_FIRST, false)
        editor.apply()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        val editor = sharedPreferences.edit()
        editor.putBoolean(Constants.IS_FIRST, false)
        editor.apply()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onUserDeniedPermission(permissionName: String) {
        super.onUserDeniedPermission(permissionName)
        val builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.location_permission_message))
        builder.setCancelable(false)
        builder.setPositiveButton(getString(R.string.ok),
            DialogInterface.OnClickListener { dialog, which ->
                askForPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),4)
                dialog.dismiss()
            })
        builder.create().show()
    }

    override fun onUserDisabledPermission(permissionName: String) {
        super.onUserDisabledPermission(permissionName)
        val builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.location_permission_message))
        builder.setCancelable(false)
        builder.setPositiveButton(getString(R.string.ok),
            DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
            })
        builder.create().show()
    }
}