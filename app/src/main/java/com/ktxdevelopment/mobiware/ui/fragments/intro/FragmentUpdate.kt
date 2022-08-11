package com.ktxdevelopment.mobiware.ui.fragments.intro

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ktxdevelopment.mobiware.R
import com.ktxdevelopment.mobiware.clients.main.BaseClient
import com.ktxdevelopment.mobiware.clients.firebase.FirebaseClient
import com.ktxdevelopment.mobiware.databinding.FragmentUpdateBinding
import com.ktxdevelopment.mobiware.ui.activities.MainActivity
import com.ktxdevelopment.mobiware.util.Constants


class FragmentUpdate : Fragment() {

    private lateinit var binding: FragmentUpdateBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentUpdateBinding.inflate(inflater, container, false)
        clickListeners()

        if (arguments != null) {
            if (arguments?.getString(Constants.UPDATE_SIGN, Constants.MAY) == Constants.MUST) {
                mustLayoutVisible()
            }else mayLayoutVisible()
        }else mayLayoutVisible()

        return binding.root
    }

    private fun clickListeners() {
        binding.btnLater.setOnClickListener {
            if (FirebaseClient.getCurrentUserId().isEmpty()) signUpIntent()
            else mainIntent()
        }

        binding.btnUpdateMay.setOnClickListener { BaseClient.playStoreIntent(requireActivity()) }
        binding.btnUpdateMust.setOnClickListener { BaseClient.playStoreIntent(requireActivity()) }
    }

    private fun mayLayoutVisible() {
        binding.llMay.visibility = View.VISIBLE
        binding.llMust.visibility = View.GONE
    }

    private fun mustLayoutVisible() {
        binding.llMay.visibility = View.GONE
        binding.llMust.visibility = View.VISIBLE
    }


    private fun mainIntent() {
        Intent(activity, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP and Intent.FLAG_ACTIVITY_NEW_TASK)
        }.also { startActivity(it); activity?.finish() }
    }

    private fun signUpIntent() {
        findNavController().navigate(R.id.action_fragmentSplash_to_fragmentSignUp)
    }
}