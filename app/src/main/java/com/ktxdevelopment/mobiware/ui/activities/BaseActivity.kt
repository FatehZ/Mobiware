package com.ktxdevelopment.mobiware.ui.activities

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.ktxdevelopment.mobiware.R
import com.ktxdevelopment.mobiware.databinding.DialogProgressBinding
import com.ktxdevelopment.mobiware.util.tryEr

open class BaseActivity: AppCompatActivity() {

    private var backPressedOnce = false
    private var mProgressDialog: Dialog? = null
    private var binding: DialogProgressBinding? = null

    fun hideKeyboard(vararg et: EditText) {
        val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        for (i in et){ imm.hideSoftInputFromWindow(i.windowToken, 0) }
    }

    fun showProgressDialog() {
        if (mProgressDialog == null) {
            binding = DialogProgressBinding.inflate(layoutInflater)
            mProgressDialog = Dialog(this).apply {
                setContentView(binding!!.root)
                setCancelable(false)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
        }
        mProgressDialog!!.show()
    }

    fun showProgressDialogCancellable() {
        tryEr {
            if (mProgressDialog == null) {
                binding = DialogProgressBinding.inflate(layoutInflater)
                mProgressDialog = Dialog(this).apply {
                    setContentView(binding!!.root)
                    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                }
            }
            mProgressDialog!!.show()
        }
    }



    fun hideProgressDialog() {
        tryEr {
            mProgressDialog?.dismiss()
        }
    }

    fun doubleBackToExit() {
        if (backPressedOnce) {
            finishAffinity()
            return
        }
        backPressedOnce = true
        shortToast(R.string.please_click_back_again)

        Handler(Looper.getMainLooper()).postDelayed({
            backPressedOnce = false
        }, 2000)
    }




    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }


    fun showErrorSnackbar(message: String) {
        val snackbar = Snackbar.make( findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        snackbar.view.setBackgroundResource(R.drawable.snackbar_error_background)
        snackbar.show()
    }

    fun showSuccessSnackbar(message: String) {
        val snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        snackbar.view.setBackgroundResource(R.drawable.snackbar_success_background)
        snackbar.show()
    }

    fun shortToast(m: Int) {
        tryEr { Toast.makeText(this, getString(m), Toast.LENGTH_SHORT).show() }
    }
}