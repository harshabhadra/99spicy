package com.a99Spicy.a99spicy.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.a99Spicy.a99spicy.BuildConfig
import com.a99Spicy.a99spicy.R
import com.a99Spicy.a99spicy.domain.LocationDetails
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import timber.log.Timber
import java.util.*

private lateinit var toolbarTextView:TextView
private lateinit var toolbar: Toolbar

private lateinit var locationDetails: LocationDetails

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        toolbar = findViewById(R.id.home_toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.title = ""
        toolbarTextView = findViewById(R.id.toolbar_TextView)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            navController.graph
        )

        navController.addOnDestinationChangedListener { controller, destination, arguments ->

            if (destination.id == navController.graph.startDestination){

            }
        }
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
        NavigationUI.setupWithNavController(navView, navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(
            navController.graph
        )
        return NavigationUI.navigateUp(navController,appBarConfiguration)
    }



    fun setAppBarElevation(elevation:Float){
        val appbarlayout: AppBarLayout = findViewById(R.id.appbar_layout)
        appbarlayout.elevation = elevation
    }

    fun setToolbarTitle(title:String){
        toolbarTextView.text = title
    }

    fun setToolbarLogo(logo: Drawable?){
        logo?.let {
            toolbar.logo = logo
        }?:let {
            toolbar.logo = null
        }
    }

    fun setToolbarBackground(color:Int){
        toolbar.setBackgroundColor(color)
    }

    fun getLocation():LocationDetails{
        return locationDetails
    }
}