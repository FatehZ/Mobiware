package com.ktxdevelopment.mobiware.ui.fragments.main

import android.app.Dialog
import android.app.UiModeManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ktxdevelopment.mobiware.R
import com.ktxdevelopment.mobiware.clients.firebase.FirebaseClient
import com.ktxdevelopment.mobiware.databinding.DialogDarkModeBinding
import com.ktxdevelopment.mobiware.databinding.DialogSettingsBinding
import com.ktxdevelopment.mobiware.databinding.FragmentSettingsBinding
import com.ktxdevelopment.mobiware.ui.activities.MainActivity
import com.ktxdevelopment.mobiware.util.tryEr
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentSettings : BaseFragment() {
     private var binding: FragmentSettingsBinding? = null
     private lateinit var dialog: Dialog
     private lateinit var dialogDark: Dialog
     private var confirmBinding: DialogSettingsBinding? = null
     private var darkBinding: DialogDarkModeBinding? = null
     private lateinit var ui: UiModeManager

     override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
          binding = FragmentSettingsBinding.inflate(inflater, container, false)
          return binding!!.root
     }


     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
          super.onViewCreated(view, savedInstanceState)
          ui = activity!!.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
          launchDialogLayouts()
          launchClickListeners()
     }

     private fun launchClickListeners() {
          binding!!.cvResetPasswordSettings.setOnClickListener { dialogResetPassword() }
          binding!!.cvNightModeSettings.setOnClickListener { dialogNightMode() }
          binding!!.cvDeleteAccountSettings.setOnClickListener { dialogDeleteAccount() }
          binding!!.cvContactSettings.setOnClickListener { launchContactIntent() }
     }

     private fun dialogResetPassword() {
          confirmBinding!!.tvDialogTitle.text = getString(R.string.reset_email_confirm_send)
          dialog.show()
          confirmBinding!!.btnYes.setOnClickListener { confirmedResetPassword() }
     }


     private fun dialogNightMode() {
          dialogDark.show()
          when(ui.currentModeType) {
               UiModeManager.MODE_NIGHT_YES -> darkBinding!!.rgSetDl.check(R.id.radDark)
               UiModeManager.MODE_NIGHT_NO -> darkBinding!!.rgSetDl.check(R.id.radLight)
               UiModeManager.MODE_NIGHT_AUTO -> darkBinding!!.rgSetDl.check(R.id.radSysDef)
          }
     }

     private fun dialogDeleteAccount() {
          confirmBinding!!.tvDialogTitle.text = "Your account will be deleted permanently"
          dialog.show()
          confirmBinding!!.btnYes.setOnClickListener { confirmedDeleteAccount() }

     }

     private fun confirmedDeleteAccount() {
          showProgressDialog()
          tryEr {
               FirebaseClient.deleteCurrentUserAccount(this)
          }
     }

     private fun confirmedResetPassword() {
          showProgressDialogCancellable()
          try {
               (activity as MainActivity).getLocalUser().observe(viewLifecycleOwner) {
                    FirebaseClient.resetPasswordWithEmail(this, it.email)
               }
          }catch (e: Exception) { hideProgressDialog() }
     }

     private fun launchContactIntent() {
          tryEr {
               Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:ktx.mobiware@gmail.com")
                    putExtra(Intent.EXTRA_SUBJECT, "Mobiware App")
               }.also{ startActivity(it) }

          }
     }

     fun onResetPasswordSuccess() { hideProgressDialog() }
     fun onReceivedError(m: String = getString(R.string.smth_went_wrong_only)) { showErrorSnackbar(m); hideProgressDialog() }

     fun onDeleteAccountSuccess() {
          hideProgressDialog()
          tryEr {
               (activity as MainActivity).getLocalViewModel().deleteUserFromFirestore(activity!!, Firebase.auth.currentUser!!.uid)
          }
          (activity as MainActivity).signOut()

     }


     private fun launchDialogLayouts() {
          confirmBinding = DialogSettingsBinding.inflate(layoutInflater)
          darkBinding = DialogDarkModeBinding.inflate(layoutInflater)

          dialog = Dialog(requireContext()).apply { window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)); setContentView(confirmBinding!!.root) }
          dialogDark = Dialog(requireContext()).apply { window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));  setContentView(darkBinding!!.root) }

          confirmBinding!!.btnNo.setOnClickListener { dialog.dismiss() }
          darkBinding!!.btnNo.setOnClickListener { dialogDark.dismiss() }

          darkBinding!!.btnYes.setOnClickListener {
               when (darkBinding!!.rgSetDl.checkedRadioButtonId) {
                    R.id.radSysDef -> {
                         ui.setApplicationNightMode(UiModeManager.MODE_NIGHT_AUTO)
                         dialogDark.dismiss()
                    }
                    R.id.radDark -> {
                         ui.setApplicationNightMode(UiModeManager.MODE_NIGHT_YES)
                         dialogDark.dismiss()
                    }
                    R.id.radLight -> {
                         ui.setApplicationNightMode(UiModeManager.MODE_NIGHT_NO)
                         dialogDark.dismiss()
                    }
               }
          }
     }





     override fun onDestroyView() {
          super.onDestroyView()
          darkBinding = null
          confirmBinding = null
          binding = null
     }
}