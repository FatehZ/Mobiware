package com.ktxdevelopment.mobiware.ui.fragments.main

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.postDelayed
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.ktxdevelopment.mobiware.R
import com.ktxdevelopment.mobiware.databinding.FragmentLatestBinding
import com.ktxdevelopment.mobiware.models.main.BrandModel
import com.ktxdevelopment.mobiware.models.rest.Resource
import com.ktxdevelopment.mobiware.ui.recview.LatestMobileAdapter
import com.ktxdevelopment.mobiware.util.Constants
import com.ktxdevelopment.mobiware.viewmodel.RetroViewModel

class FragmentBrandPhones : BaseFragment(), LatestMobileAdapter.OnMobileClickListener {
     private lateinit var binding: FragmentLatestBinding
     private lateinit var bmAdapter: LatestMobileAdapter
     private lateinit var restViewModel: RetroViewModel
     private var errorMessageShown = false

     override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
          binding = FragmentLatestBinding.inflate(inflater, container, false)
          return binding.root
     }


     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
          super.onViewCreated(view, savedInstanceState)
          subscribeUI()

          val brand = arguments!!.getParcelable<BrandModel>(Constants.BRAND_EXTRA)!!
          restViewModel.searchByBrand(brand.brand_slug,0)


          restViewModel.devicesByBrand.observe(requireActivity()) {
               when (it) {
                    is Resource.Success -> {
                         binding.shimmerLayout.visibility = View.GONE
                         binding.shimmerLayout.stopShimmer()
                         binding.rvLatest.visibility = View.VISIBLE
                         bmAdapter.submitList(it.data!!.data.phones)
                    }
                    is Resource.Error -> Handler(Looper.getMainLooper()).postDelayed(500) { searchAgain() }
                    else -> Unit
               }
          }
     }



     private fun searchAgain() {
          restViewModel.searchLatest()
     }

     private fun subscribeUI() {
          bmAdapter = LatestMobileAdapter(this)
          restViewModel = ViewModelProvider(requireActivity())[RetroViewModel::class.java]

          binding.rvLatest.apply {
               layoutManager = GridLayoutManager(requireContext(), 2)
               adapter = bmAdapter }
     }

     override fun onPosClick(pos: Int) {
          val bundle = Bundle().apply { putString(Constants.PHONE_EXTRA, bmAdapter.currentList[pos].slug) }
          findNavController().navigate(R.id.action_fragmentBrandPhones_to_fragmentSecondaryHardware, bundle)
     }

     override fun onDetach() {
          super.onDetach()
          errorMessageShown = false
     }

}