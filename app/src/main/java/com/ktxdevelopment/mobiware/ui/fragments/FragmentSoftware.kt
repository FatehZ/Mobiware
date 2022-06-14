package com.ktxdevelopment.mobiware.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ktxdevelopment.mobiware.R
import com.ktxdevelopment.mobiware.databinding.FragmentSoftwareBinding

class FragmentSoftware : BaseFragment() {
    private lateinit var binding: FragmentSoftwareBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSoftwareBinding.inflate(inflater)
        return inflater.inflate(R.layout.fragment_hardware, container, false)
    }
}