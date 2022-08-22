package com.ktxdevelopment.mobiware.ui.fragments.main

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.os.postDelayed
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.ktxdevelopment.mobiware.R
import com.ktxdevelopment.mobiware.databinding.FragmentPhoneListPaginatedBinding
import com.ktxdevelopment.mobiware.models.main.BrandModel
import com.ktxdevelopment.mobiware.models.rest.Resource
import com.ktxdevelopment.mobiware.ui.recview.LatestMobileAdapter
import com.ktxdevelopment.mobiware.util.Constants
import com.ktxdevelopment.mobiware.viewmodel.RetroViewModel

class FragmentBrandPhones : BaseFragment(), LatestMobileAdapter.OnMobileClickListener {
     private lateinit var binding: FragmentPhoneListPaginatedBinding
     private lateinit var bmAdapter: LatestMobileAdapter
     private lateinit var restViewModel: RetroViewModel
     private var errorMessageShown = false
     private lateinit var brand: BrandModel
     private var currentPage: Int = 0

     override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
          binding = FragmentPhoneListPaginatedBinding.inflate(inflater, container, false)
          return binding.root
     }


     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
          super.onViewCreated(view, savedInstanceState)
          initializeUI()
          launchOnlineSearch()
     }

     private fun launchOnlineSearch() {
          brand = arguments!!.getParcelable(Constants.BRAND_EXTRA)!!
          getPageSizeOfBrand(brand)
          setActionBarTitle(brand.brand_name.replaceFirstChar { it.uppercaseChar() })
          restViewModel.searchByBrand(brand.brand_slug,currentPage)


          restViewModel.devicesByBrand.observe(requireActivity()) {
               when (it) {
                    is Resource.Success -> {
                         bmAdapter.submitList(it.data!!.data.phones)
                         Handler(Looper.getMainLooper()).postDelayed(500) { loadMainVisible() }
                    }
                    is Resource.Error -> Handler(Looper.getMainLooper()).postDelayed(500) { searchAgain() }
                    else -> Unit
               }
          }
     }

     private fun loadMainVisible() {
          binding.shimmerLayout.visibility = GONE
          binding.shimmerLayout.stopShimmer()
          binding.mainContentLayout.visibility = VISIBLE
     }

     private fun loadShimmerVisible() {
          binding.shimmerLayout.visibility = VISIBLE
          binding.shimmerLayout.startShimmer()
          binding.mainContentLayout.visibility = GONE
     }


     private fun searchAgain() {
          restViewModel.searchByBrand(brand.brand_slug,currentPage)
     }

     private fun initializeUI() {
          binding.fabPageCounter.text = currentPage.toString()
          bmAdapter = LatestMobileAdapter(this)
          restViewModel = ViewModelProvider(requireActivity())[RetroViewModel::class.java]

          binding.rvLatest.apply {
               layoutManager = GridLayoutManager(requireContext(), 2)
               adapter = bmAdapter }

          binding.btnNextPage.setOnClickListener {
               currentPage += 1
               onPageChanged(currentPage)
          }

          binding.btnPreviousPage.setOnClickListener {
               currentPage -= 1
               onPageChanged(currentPage)
          }
     }

     private fun onPageChanged(page: Int) {
          binding.fabPageCounter.text = (currentPage + 1).toString()

          if (currentPage == 0) {
               binding.btnPreviousPage.visibility = GONE
          }else  binding.btnPreviousPage.visibility = VISIBLE


          if (currentPage == getPageSizeOfBrand(brand) - 1) {
               binding.btnNextPage.visibility = GONE
          }else  binding.btnNextPage.visibility = VISIBLE

          restViewModel.searchResponse.value = null
          loadShimmerVisible()
          restViewModel.searchByBrand(brand.brand_slug, page)
     }

     override fun onPosClick(pos: Int) {
          val bundle = Bundle().apply { putString(Constants.PHONE_EXTRA, bmAdapter.currentList[pos].slug) }
          setActionBarTitle(bmAdapter.currentList[pos].brand + bmAdapter.currentList[pos].phone_name)
          findNavController().navigate(R.id.action_fragmentBrandPhones_to_fragmentSecondaryHardware, bundle)
     }

     override fun onDetach() {
          super.onDetach()
          errorMessageShown = false
     }

     private fun getPageSizeOfBrand(brand: BrandModel) : Int {
          return if (brand.device_count % 40 != 0) brand.device_count / 40 + 1
          else brand.device_count / 40
     }
}