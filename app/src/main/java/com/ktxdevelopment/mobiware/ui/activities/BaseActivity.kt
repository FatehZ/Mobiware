package com.ktxdevelopment.mobiware.ui.activities

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.ktxdevelopment.mobiware.R
import com.ktxdevelopment.mobiware.databinding.DialogProgressBinding

open class BaseActivity: AppCompatActivity() {

    private var backPressedOnce = false
    private var mProgressDialog: Dialog? = null
    private lateinit var binding: DialogProgressBinding

    fun hidePhoneBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            WindowManager.LayoutParams.FLAG_FULLSCREEN
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        }
    }


    fun showProgressDialog() {

        if (mProgressDialog == null) {
            mProgressDialog = Dialog(this).apply {
                setContentView(binding.root)
                setCancelable(false)
                setOnCancelListener { doubleBackToExit() }
            }
            mProgressDialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        mProgressDialog!!.show()


    }

    fun hideProgressDialog() {
        mProgressDialog?.dismiss()
    }

    fun doubleBackToExit() {
        if (backPressedOnce) {
            super.onBackPressed()
            return
        }
        backPressedOnce = true
        Toast.makeText(
            this,
            resources.getString(R.string.please_click_back_again),
            Toast.LENGTH_LONG
        ).show()

        Handler(Looper.getMainLooper()).postDelayed({
            backPressedOnce = false
        }, 2000)
    }

    fun showErrorSnackbar(message: String) {
        val snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        snackbar.view.setBackgroundColor(-0xffaa00)
        snackbar.show()
    }
}