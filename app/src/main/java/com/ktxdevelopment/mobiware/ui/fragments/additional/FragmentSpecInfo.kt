package com.ktxdevelopment.mobiware.ui.fragments.additional

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ktxdevelopment.mobiware.databinding.FragmentSpecInfoBinding
import com.ktxdevelopment.mobiware.ui.fragments.main.BaseFragment

class FragmentSpecInfo : BaseFragment() {

     private var _binding: FragmentSpecInfoBinding? = null
     private val binding get() = _binding!!

     override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
          _binding = FragmentSpecInfoBinding.inflate(inflater, container, false)
          return binding.root
     }

}