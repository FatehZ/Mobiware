package com.ktxdevelopment.mobiware.ui.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.ktxdevelopment.mobiware.clients.ui.MainActivityClient
import com.ktxdevelopment.mobiware.databinding.ActivityMainBinding
import com.ktxdevelopment.mobiware.models.rest.product.Data
import com.ktxdevelopment.mobiware.util.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var mainBinding: ActivityMainBinding
    private val TAG = "MAIN_TAG"
    private lateinit var phoneDetails: Data

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater).also { setContentView(it.root) }
        setupPrimaryUI()
        setupNavUI()

        var phoneDetails = intent.getParcelableExtra<Data>(Constants.PHONE_EXTRA)

    }




    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return MainActivityClient.setUpMainNavigationClickListeners(item, this@MainActivity)
    }

    private fun setupPrimaryUI() {
        hidePhoneBar()
        setSupportActionBar(mainBinding.toolbarMainActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mainBinding.toolbarMainActivity.setNavigationOnClickListener { MainActivityClient.toggleDrawer(mainBinding) }
        mainBinding.navView.setNavigationItemSelectedListener(this)

    }

    private fun setupNavUI() {

    }




    fun closeDrawer() {
        if (mainBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) mainBinding.drawerLayout.closeDrawer(
            GravityCompat.START
        )
    }

    override fun onBackPressed() {
        if (mainBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) closeDrawer()
        else doubleBackToExit()
    }
}