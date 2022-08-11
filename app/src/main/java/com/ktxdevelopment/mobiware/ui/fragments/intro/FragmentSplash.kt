package com.ktxdevelopment.mobiware.ui.fragments.intro

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ktxdevelopment.mobiware.databinding.FragmentSplashBinding
import com.ktxdevelopment.mobiware.ui.fragments.main.BaseFragment


class FragmentSplash : BaseFragment() {

     private lateinit var binding: FragmentSplashBinding
     private val handler = Handler(Looper.getMainLooper())
     private var duration = 0L

     override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
          binding = FragmentSplashBinding.inflate(inflater,container, false)
          return binding.root
     }
}