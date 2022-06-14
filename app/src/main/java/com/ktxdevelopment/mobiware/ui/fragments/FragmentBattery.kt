package com.ktxdevelopment.mobiware.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ktxdevelopment.mobiware.databinding.FragmentBatteryBinding

class FragmentBattery : BaseFragment() {
    private lateinit var binding: FragmentBatteryBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentBatteryBinding.inflate(inflater)
        return binding.root
    }
}
