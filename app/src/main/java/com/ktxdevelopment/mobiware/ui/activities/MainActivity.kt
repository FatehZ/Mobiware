package com.ktxdevelopment.mobiware.ui.activities

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.core.view.GravityCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.ktxdevelopment.mobiware.R
import com.ktxdevelopment.mobiware.clients.ui.MainActivityClient
import com.ktxdevelopment.mobiware.databinding.ActivityMainBinding
import com.ktxdevelopment.mobiware.databinding.NavHeaderBinding
import com.ktxdevelopment.mobiware.models.rest.product.Data
import com.ktxdevelopment.mobiware.util.Constants
import com.ktxdevelopment.mobiware.viewmodel.LocalViewModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var mainBinding: ActivityMainBinding
    private val TAG = "MAIN_TAG"
    private lateinit var phoneDetails: Data
    private lateinit var viewModel: LocalViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        mainBinding = ActivityMainBinding.inflate(layoutInflater).also { setContentView(it.root) }
        viewModel = ViewModelProvider(this)[LocalViewModel::class.java]
        setupPrimaryUI()
        setupNavUI()



        if (intent.hasExtra(Constants.PHONE_EXTRA)) {
            phoneDetails = intent.getParcelableExtra(Constants.PHONE_EXTRA)!!

            viewModel.writeMobileToRoomDB(this, phoneDetails)

        }else{
            viewModel.getRoomMobileDetails()
            viewModel.roomMobileDetails.observe(this) {
                //todo  send to fragment or declare in fragment -- mostly second one
            }
        }
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
        viewModel.getLocalUser(this)
        val headerBinding = NavHeaderBinding.bind(mainBinding.navView.getHeaderView(0))

        viewModel.localUser.observe(this) {
            headerBinding.tvUserName.text = it.username
            Glide.with(this)
                .load(it.image)
                .placeholder(R.drawable.ic_account)
                .into(headerBinding.ivNavProfileImage)
        }
    }




    fun closeDrawer() {
        if (mainBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) mainBinding.drawerLayout.closeDrawer(GravityCompat.START)
    }

    override fun onBackPressed() {
        if (mainBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) closeDrawer()
        else doubleBackToExit()
    }
}