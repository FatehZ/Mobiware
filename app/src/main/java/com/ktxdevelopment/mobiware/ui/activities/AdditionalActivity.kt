package com.ktxdevelopment.mobiware.ui.activities

import android.annotation.TargetApi
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.navigation.findNavController
import com.ktxdevelopment.mobiware.R
import com.ktxdevelopment.mobiware.databinding.ActivityAdditionalBinding
import com.ktxdevelopment.mobiware.databinding.DialogSettingsBinding
import com.ktxdevelopment.mobiware.util.Constants
import com.ktxdevelopment.mobiware.util.Constants.FR_FEEDBACK
import com.ktxdevelopment.mobiware.util.Constants.FR_PROFILE
import com.ktxdevelopment.mobiware.util.tryEr
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdditionalActivity : BaseActivity() {
     private lateinit var binding: ActivityAdditionalBinding

     override fun onCreate(savedInstanceState: Bundle?) {
          super.onCreate(savedInstanceState)
          binding = ActivityAdditionalBinding.inflate(layoutInflater).also { setContentView(it.root) }
          tryEr { handleDestinationOfIntent(intent) }

          binding.btnBack.setOnClickListener { finish() }

     }

     private fun handleDestinationOfIntent(i: Intent) {
          when (i.action) {
               FR_FEEDBACK -> {
                    findNavController(R.id.navHostAdditional).navigate(R.id.actionToFeedback)
                    binding.titleAdditional.text = getString(R.string.feedback)
               }
               FR_PROFILE -> {
                    findNavController(R.id.navHostAdditional).navigate(R.id.actionToProfile)
                    binding.titleAdditional.text = getString(R.string.profile)
               }
          }
     }


     override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
          super.onRequestPermissionsResult(requestCode, permissions, grantResults)
          if (requestCode == Constants.READ_STORAGE_CODE) {
               if (grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) showPermissionDeniedDialog()
          }
     }


     private fun showPermissionDeniedDialog() {
          val dBind = DialogSettingsBinding.inflate(layoutInflater)
          val dialog = Dialog(this).apply { window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)); setContentView(dBind.root) }
          dBind.tvDialogTitle.text = getString(R.string.denied_permission)
          dBind.btnNo.setOnClickListener { dialog.dismiss() }
          dBind.btnYes.setOnClickListener { settingsPermissionIntent() }
          dialog.show()
     }

     private fun settingsPermissionIntent() {
          Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", packageName, null)).apply {
               addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
          }.also { startActivity(it) }
     }

     @Deprecated("Deprecated in Java", ReplaceWith("finish()"))
     override fun onBackPressed() = finish()
}