package com.ktxdevelopment.mobiware.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ktxdevelopment.mobiware.databinding.FragmentHardwareBinding

class FragmentHardware : BaseFragment() {
    private lateinit var binding: FragmentHardwareBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHardwareBinding.inflate(inflater)
        return binding.root
    }
}