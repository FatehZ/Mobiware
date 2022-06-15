package com.ktxdevelopment.mobiware.clients.ui

import android.content.Intent
import android.view.MenuItem
import androidx.core.view.GravityCompat
import androidx.navigation.findNavController
import com.ktxdevelopment.mobiware.R
import com.ktxdevelopment.mobiware.clients.Preferences
import com.ktxdevelopment.mobiware.clients.firebase.FirebaseClient
import com.ktxdevelopment.mobiware.databinding.ActivityMainBinding
import com.ktxdevelopment.mobiware.ui.activities.FeedbackActivity
import com.ktxdevelopment.mobiware.ui.activities.MainActivity
import com.ktxdevelopment.mobiware.ui.activities.ProfileActivity
import com.ktxdevelopment.mobiware.ui.activities.SignInActivity

object MainActivityClient {

    fun toggleDrawer(binding: ActivityMainBinding) {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) binding.drawerLayout.closeDrawer(
            GravityCompat.START
        )
        else binding.drawerLayout.openDrawer(GravityCompat.START)
    }

    fun setUpMainNavigationClickListeners(item:MenuItem, context: MainActivity): Boolean {
        return when (item.itemId) {
            R.id.ic_nav_settings -> {
                context.startActivity(
                    Intent(
                        context,
                        ProfileActivity::class.java
                    )
                ); context.closeDrawer();true
            }
            R.id.ic_nav_feedback -> {
                context.startActivity(
                    Intent(
                        context,
                        FeedbackActivity::class.java
                    )
                ); context.closeDrawer();true
            }
            R.id.ic_nav_hardware -> {
                context.findNavController(R.id.navHost).navigate(R.id.actionToHardware); context.closeDrawer(); true
            }
            R.id.ic_nav_software -> {
                context.findNavController(R.id.navHost).navigate(R.id.actionToSoftware); context.closeDrawer(); true
            }
            R.id.ic_nav_battery -> {
                context.findNavController(R.id.navHost).navigate(R.id.actionToBattery); context.closeDrawer(); true
            }
            R.id.ic_nav_sign_out -> {
                FirebaseClient.signOut()
                Intent(context, SignInActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    .also { context.startActivity(it); context.finish() }
                true;
            }
            else -> false
        }
    }
}