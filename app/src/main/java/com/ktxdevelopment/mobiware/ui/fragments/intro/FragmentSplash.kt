package com.ktxdevelopment.mobiware.ui.fragments.intro

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.postDelayed
import androidx.navigation.fragment.findNavController
import com.ktxdevelopment.mobiware.R
import com.ktxdevelopment.mobiware.clients.RemoteClient
import com.ktxdevelopment.mobiware.clients.firebase.FirebaseClient
import com.ktxdevelopment.mobiware.databinding.FragmentSplashBinding
import com.ktxdevelopment.mobiware.ui.activities.MainActivity
import com.ktxdevelopment.mobiware.ui.fragments.main.BaseFragment
import com.ktxdevelopment.mobiware.util.Constants


class FragmentSplash : BaseFragment() {

    private lateinit var binding: FragmentSplashBinding
    private val handler = Handler(Looper.getMainLooper())
    private var duration = 3000L

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSplashBinding.inflate(inflater,container, false)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S) duration = 0L
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { duration = 0L }
        checkForUpdate()
    }


    private fun mainIntent() {
        handler.postDelayed(duration) {
            Intent(activity, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP and Intent.FLAG_ACTIVITY_NEW_TASK)
            }.also { startActivity(it); activity?.finish() }
        }
    }

    private fun signUpIntent() {
        handler.postDelayed(duration) {
            findNavController().navigate(R.id.actionToFragmentSignUp)
        }
    }

    private fun updateIntent(sign: String) {
        handler.postDelayed(duration) {
            val bundle = Bundle().apply { putString(Constants.UPDATE_SIGN, sign) }
            findNavController().navigate(R.id.action_fragmentSplash_to_fragmentUpdate, bundle)
        }
    }

    private fun checkForUpdate() {
        val map = RemoteClient.checkMustUpdate()
        if (map[Constants.ANY_UPDATE] as Boolean) updateIntent(map[Constants.UPDATE_SIGN] as String)
        else if (FirebaseClient.getCurrentUserId().isEmpty()) signUpIntent()
        else mainIntent()
    }
}