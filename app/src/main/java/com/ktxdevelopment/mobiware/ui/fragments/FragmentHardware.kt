package com.ktxdevelopment.mobiware.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.ktxdevelopment.mobiware.databinding.FragmentHardwareBinding
import com.ktxdevelopment.mobiware.models.rest.product.Data
import com.ktxdevelopment.mobiware.viewmodel.LocalViewModel

class FragmentHardware : BaseFragment() {
    private lateinit var binding: FragmentHardwareBinding
    private lateinit var roomViewModel: LocalViewModel
    private lateinit var mobile: Data

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHardwareBinding.inflate(inflater)
        roomViewModel = ViewModelProvider(this)[LocalViewModel::class.java]
        roomViewModel.getRoomMobileDetails()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        roomViewModel.roomMobileDetails.observe(viewLifecycleOwner) { if (it != null) mobile = it.data }

    }
}