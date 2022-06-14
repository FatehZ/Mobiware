package com.ktxdevelopment.mobiware.ui.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.ktxdevelopment.mobiware.clients.ui.MainActivityClient
import com.ktxdevelopment.mobiware.databinding.ActivityMainBinding
import com.ktxdevelopment.mobiware.models.firebase.FireUser
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var mainBinding: ActivityMainBinding
    private val TAG = "MAIN_TAG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        doubleBackToExit()
        mainBinding = ActivityMainBinding.inflate(layoutInflater).also { setContentView(it.root) }
        setupPrimaryUI()




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



    fun closeDrawer() {
        if (mainBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) mainBinding.drawerLayout.closeDrawer(
            GravityCompat.START
        )
    }

    override fun onBackPressed() {
        if (mainBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) closeDrawer()
        else doubleBackToExit()
    }

    fun loadUserMain(loggedUser: FireUser) {

    }
}