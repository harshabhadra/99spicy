package com.a99Spicy.a99spicy.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.a99Spicy.a99spicy.BuildConfig
import com.a99Spicy.a99spicy.R
import com.a99Spicy.a99spicy.domain.LocationDetails
import com.a99Spicy.a99spicy.network.Profile
import com.a99Spicy.a99spicy.utils.Constants
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.location_permission_layuot.view.*
import timber.log.Timber
import java.util.*

private lateinit var toolbarTextView: TextView
private lateinit var toolbar: Toolbar
private lateinit var userId: String

private var locationDetails: LocationDetails? = null
private var profile:Profile? = null

class HomeActivity : AppCompatActivity() {
    companion object {
        const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        toolbar = findViewById(R.id.home_toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.title = ""
        toolbarTextView = findViewById(R.id.toolbar_TextView)

        val intent = intent
        intent?.let {
            userId = intent.getStringExtra(Constants.USER_ID)!!
            profile = intent.getParcelableExtra(Constants.PROFILE)
            Toast.makeText(applicationContext, "user id: $userId", Toast.LENGTH_SHORT).show()
        }

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            navController.graph
        )

        navController.addOnDestinationChangedListener { controller, destination, arguments ->

            if (destination.id == navController.graph.startDestination) {
                navView.visibility = View.VISIBLE
            } else {
                navView.visibility = View.GONE
            }
        }
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
        NavigationUI.setupWithNavController(navView, navController)


        //Request location permission
        if (foregroundPermissionApproved()) {
            getLocationDetails()
        } else {
            showLocationDialog()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(
            navController.graph
        )
        return NavigationUI.navigateUp(navController, appBarConfiguration)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE -> when {
                grantResults.isEmpty() ->
                    // If user interaction was interrupted, the permission request
                    // is cancelled and you receive empty arrays.
                    Timber.e("User interaction was cancelled.")
                grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
                    // Permission was granted.
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT)
                        .show()
                    getLocationDetails()
                }
                else -> {
                    // Permission denied
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage(getString(R.string.permission_denied_explanation))
                    builder.setCancelable(false)
                    builder.setPositiveButton(
                        getString(R.string.settings),
                        DialogInterface.OnClickListener { dialog, which ->
                            // Build intent that displays the App settings screen.
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri = Uri.fromParts(
                                "package",
                                BuildConfig.APPLICATION_ID,
                                null
                            )
                            intent.data = uri
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            dialog.dismiss()
                        })
                    val dialog = builder.create()
                    dialog.show()
                }
            }
        }
    }

    private fun foregroundPermissionApproved(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    private fun requestForegroundPermissions() {
        val provideRationale = foregroundPermissionApproved()

        // If the user denied a previous request, but didn't check "Don't ask again", provide
        // additional rationale.
        if (provideRationale) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage(getString(R.string.permission_rationale))
            builder.setCancelable(false)
            builder.setPositiveButton(getString(R.string.ok),
                DialogInterface.OnClickListener { dialog, which ->
                    // Request permission
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
                    )
                    dialog.dismiss()
                })
            builder.create().show()
        } else {
            Timber.e("Request foreground only permission")
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocationDetails() {

        val locationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (isGpsEnabled) {
            val location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            location?.let {

                val geocoder = Geocoder(this, Locale.getDefault())
                val addressList = geocoder.getFromLocation(it.latitude, it.longitude, 1)

                val postCode = addressList[0].postalCode
                val city = addressList[0].locality
                val district = addressList[0].subAdminArea
                val state = addressList[0].adminArea

                locationDetails = LocationDetails(postCode, city, district, state)
            }
        } else {
            //Create an alert dialog if the location is off on device
            val builder =
                AlertDialog.Builder(this)
            builder.setCancelable(false)
            builder.setMessage("Your Location is disabled. Turn on your location")
            builder.setPositiveButton(
                "ok"
            ) { dialog, which -> startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }

            val dialog = builder.create()
            dialog.show()
        }
    }

    private fun showLocationDialog() {
        val layout = LayoutInflater.from(this).inflate(R.layout.location_permission_layuot, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(layout)
        builder.setCancelable(false)
        val okButton = layout.ok_location_button
        val dialog = builder.create()
        dialog.show()

        okButton.setOnClickListener {
            requestForegroundPermissions()
            dialog.dismiss()
        }
    }

    fun setAppBarElevation(elevation: Float) {
        val appbarlayout: AppBarLayout = findViewById(R.id.appbar_layout)
        appbarlayout.elevation = elevation
    }

    fun setToolbarTitle(title: String) {
        toolbarTextView.text = title
    }

    fun setToolbarLogo(logo: Drawable?) {
        logo?.let {
            toolbar.logo = logo
        } ?: let {
            toolbar.logo = null
        }
    }

    fun setToolbarBackground(color: Int) {
        toolbar.setBackgroundColor(color)
    }

    fun getLocation(): LocationDetails? {
        return locationDetails
    }

    fun getUserId(): String {
        return userId
    }

    fun getProfile():Profile?{
        return profile
    }
}