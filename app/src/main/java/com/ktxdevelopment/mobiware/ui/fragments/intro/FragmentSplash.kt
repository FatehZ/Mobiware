package com.ktxdevelopment.mobiware.ui.fragments.intro

import android.content.Intent
import android.os.Build
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
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
     private var duration = 0L

     override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
          binding = FragmentSplashBinding.inflate(inflater,container, false)
          return binding.root
     }
}