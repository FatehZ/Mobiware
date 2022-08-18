package com.ktxdevelopment.mobiware.ui.fragments.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.ktxdevelopment.mobiware.R
import com.ktxdevelopment.mobiware.databinding.FragmentBrandsBinding
import com.ktxdevelopment.mobiware.models.main.BrandModel
import com.ktxdevelopment.mobiware.models.main.BrandsResponse
import com.ktxdevelopment.mobiware.ui.recview.BrandListAdapter
import com.ktxdevelopment.mobiware.util.Constants
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FragmentBrands : BaseFragment(), BrandListAdapter.OnBrandClickListener {

     private var _binding: FragmentBrandsBinding? = null
     private val binding get() = _binding!!
     private lateinit var brandAdapter: BrandListAdapter
     private lateinit var brandList: ArrayList<BrandModel>

     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
          super.onViewCreated(view, savedInstanceState)
          brandAdapter = BrandListAdapter(this)
          binding.rvBrands.apply { this.adapter = brandAdapter; layoutManager = GridLayoutManager(requireContext(), 2) }
          brandList = Gson().fromJson(resources.openRawResource(R.raw.brands).bufferedReader().use { it.readText() }, BrandsResponse::class.java).data.also { brandAdapter.setData(it) }
     }


     override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View { _binding = FragmentBrandsBinding.inflate(inflater, container, false)
          return binding.root
     }

     override fun onDestroyView() {
          super.onDestroyView()
          _binding = null
     }

     override fun onBrandClick(brand: BrandModel) {
          val bundle = Bundle().apply { putParcelable(Constants.BRAND_EXTRA, brand) }
          findNavController().navigate(R.id.action_fragmentBrands_to_fragmentBrandPhones, bundle)
     }
}