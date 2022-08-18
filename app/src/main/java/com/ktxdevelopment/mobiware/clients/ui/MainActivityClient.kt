package com.ktxdevelopment.mobiware.clients.ui

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import androidx.core.os.postDelayed
import androidx.core.view.GravityCompat.START
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.ktxdevelopment.mobiware.R
import com.ktxdevelopment.mobiware.databinding.ActivityMainBinding
import com.ktxdevelopment.mobiware.ui.activities.AdditionalActivity
import com.ktxdevelopment.mobiware.ui.activities.MainActivity
import com.ktxdevelopment.mobiware.util.Constants

object MainActivityClient {
     private val hl = Handler(Looper.getMainLooper())

     fun toggleDrawer(binding: ActivityMainBinding) {
          if (binding.drawerLayout.isDrawerOpen(START)) binding.drawerLayout.closeDrawer(START)
          else binding.drawerLayout.openDrawer(START)
     }

     fun setUpMainNavigationClickListeners(item: MenuItem, context: MainActivity): Boolean {
          return when (item.itemId) {

               R.id.ic_nav_profile -> {
                    context.closeDrawer()
                    launchProfileIntent(context)
                    true
               }
               R.id.ic_nav_feedback -> {
                    context.closeDrawer()
                    launchFeedbackIntent(context)
                    true
               }
               R.id.ic_nav_hardware -> {
                    if (context.findNavController(R.id.navHost).currentDestination!!.id == R.id.fragmentHardware) context.closeDrawer()
                    else {
                         context.closeDrawer()
                         navigateToHardware(context)
                    }
                    true
               }
               R.id.ic_nav_sign_out -> {
                    context.signOut()
                    true
               }
               R.id.ic_nav_latest -> {
                    when (context.findNavController(R.id.navHost).currentDestination!!.id) {
                         R.id.fragmentLatest -> context.closeDrawer()
                         else -> {
                              context.closeDrawer()
                              navigateToLatest(context)
                         }
                    }
                    true
               }
               R.id.ic_nav_my_devices -> {
                    when (context.findNavController(R.id.navHost).currentDestination!!.id) {
                         R.id.fragmentMyDevices -> context.closeDrawer()
                         else -> {
                              context.closeDrawer()
                              navigateToMyDevices(context)
                         }
                    }
                    true
               }
               R.id.ic_nav_settings -> {
                    when (context.findNavController(R.id.navHost).currentDestination!!.id) {
                         R.id.fragmentSettings -> context.closeDrawer()
                         else -> {
                              context.closeDrawer()
                              navigateToSettings(context)
                         }
                    }
                    true
               }
               R.id.ic_nav_brands -> {
                    when (context.findNavController(R.id.navHost).currentDestination!!.id) {
                         R.id.fragmentBrands -> context.closeDrawer()
                         else -> {
                              context.closeDrawer()
                              navigateToBrands(context)
                         }
                    }
                    true
               }
               else -> false
          }
     }


     private fun navigateToMyDevices(context: MainActivity) {
          hl.postDelayed(200) {
               context.findNavController(R.id.navHost)
                    .navigate(R.id.actionToMyDevices)
               context.supportActionBar?.title = context.getString(R.string.my_devices)
          }
     }

     private fun navigateToLatest(context: MainActivity) {
          hl.postDelayed(200) {
               context.findNavController(R.id.navHost)
                    .navigate(R.id.actionToLatest)
               context.supportActionBar?.title = context.getString(R.string.latest_devices)
          }
     }


     private fun navigateToHardware(context: MainActivity) {
          hl.postDelayed(200) {
               context.findNavController(R.id.navHost)
                    .navigate(R.id.actionToHardware)
               context.supportActionBar?.title = context.getString(R.string.app_name)
          }
     }

     private fun navigateToSettings(context: MainActivity) {
          hl.postDelayed(200) {
               context.findNavController(R.id.navHost).navigate(R.id.actionToSettings)
               context.supportActionBar?.title = context.getString(R.string.settings)
          }
     }

     private fun navigateToBrands(context: MainActivity) {
          hl.postDelayed(200) {
               context.findNavController(R.id.navHost).navigate(R.id.actionToBrands)
               context.supportActionBar?.title = context.getString(R.string.brands)
          }
     }

     private fun launchFeedbackIntent(context: MainActivity) {
          hl.postDelayed(200) {
               Intent(context, AdditionalActivity::class.java).apply {
                    action = Constants.FR_FEEDBACK
                    putExtra(Constants.USER_EXTRA, context.getLocalUser().value?.email)
               }.also { context.startActivity(it) }
          }
     }

     fun launchProfileIntent(context: MainActivity) {
          hl.postDelayed(200) {
               Intent(context, AdditionalActivity::class.java).apply {
                    action = Constants.FR_PROFILE
               }.also { context.startActivity(it) }
          }
     }

     fun onCustomBackPressed(activity: MainActivity, mainBinding: ActivityMainBinding, nav: NavController) {
          if (mainBinding.drawerLayout.isDrawerOpen(START)) activity.closeDrawer()
          else {
               nav.apply {
                    when (currentDestination!!.id) {
                         R.id.fragmentHardware -> activity.doubleBackToExit()
                         R.id.fragmentSecondaryHardware -> this.navigateUp()
                         R.id.fragmentBrandPhones -> this.navigateUp()
                         else -> navigate(R.id.actionToHardware)
                    }
               }
          }
     }
}