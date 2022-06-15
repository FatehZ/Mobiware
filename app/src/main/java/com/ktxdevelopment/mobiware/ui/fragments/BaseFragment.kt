package com.ktxdevelopment.mobiware.ui.fragments

import android.app.Dialog
import androidx.fragment.app.Fragment
import com.ktxdevelopment.mobiware.R
import com.ktxdevelopment.mobiware.databinding.DialogProgressBinding

open class BaseFragment : Fragment() {

    private var mProgressDialog: Dialog? = null
    private lateinit var binding: DialogProgressBinding

    fun showProgressDialog(text: String = getString(R.string.please_wait)) {

        if (mProgressDialog == null) {
            binding = DialogProgressBinding.inflate(layoutInflater)
            mProgressDialog = Dialog(requireContext()).apply { setContentView(binding.root) }
        }
        mProgressDialog!!.show()
    }

    fun hideProgressDialog() {
        mProgressDialog?.dismiss()
    }
}