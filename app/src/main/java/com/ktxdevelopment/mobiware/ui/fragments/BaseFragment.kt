package com.ktxdevelopment.mobiware.ui.fragments

import android.app.Dialog
import androidx.fragment.app.Fragment
import com.ktxdevelopment.mobiware.R
import com.ktxdevelopment.mobiware.databinding.DialogProgressBinding

open class BaseFragment : Fragment() {

    private lateinit var mProgressDialog: Dialog
    private lateinit var binding: DialogProgressBinding

    fun showProgressDialog(text: String = getString(R.string.please_wait)) {

        binding = DialogProgressBinding.inflate(layoutInflater).apply { textViewDialog.text = text }
        mProgressDialog = Dialog(requireContext()).apply { setContentView(binding.root) }
        mProgressDialog.show()
    }

    fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }
}