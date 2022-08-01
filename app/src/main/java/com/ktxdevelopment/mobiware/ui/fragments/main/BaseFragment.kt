package com.ktxdevelopment.mobiware.ui.fragments.main

import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ktxdevelopment.mobiware.R
import com.ktxdevelopment.mobiware.databinding.DialogProgressBinding
import com.ktxdevelopment.mobiware.ui.activities.BaseActivity
import com.ktxdevelopment.mobiware.ui.activities.IntroductionActivity
import com.ktxdevelopment.mobiware.util.tryEr
import kotlin.coroutines.coroutineContext

open class BaseFragment : Fragment() {

     private lateinit var binding: DialogProgressBinding

     fun showProgressDialog(text: String = getString(R.string.please_wait)) {
          tryEr {
               (activity as BaseActivity).showProgressDialog()
          }
     }

     fun hideProgressDialog() {
          tryEr {
               (activity as BaseActivity).hideProgressDialog()
          }
     }

     fun showProgressDialogCancellable(text: String = getString(R.string.please_wait)) {
          tryEr {
               (activity as BaseActivity).showProgressDialogCancellable()
          }
     }

     fun showErrorSnackbar(text: String) {
          tryEr { (activity as BaseActivity).showErrorSnackbar(text) }
     }

     fun showSuccessSnackbar(text: String) {
          tryEr { (activity as BaseActivity).showSuccessSnackbar(text) }
     }

     fun shortToast(m: Int) {
          tryEr { Toast.makeText(context!!, getString(m), Toast.LENGTH_SHORT).show() }
     }

     fun signOut() {
          tryEr {
               (activity as IntroductionActivity).signOut()
          }
     }
}