package com.ktxdevelopment.mobiware.ui.fragments.main

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.os.postDelayed
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.ktxdevelopment.mobiware.R
import com.ktxdevelopment.mobiware.databinding.FragmentMyDevicesBinding
import com.ktxdevelopment.mobiware.ui.activities.MainActivity
import com.ktxdevelopment.mobiware.ui.recview.LatestMobileAdapter
import com.ktxdevelopment.mobiware.util.Constants
import com.ktxdevelopment.mobiware.viewmodel.RetroViewModel

class FragmentMyDevices: BaseFragment(), LatestMobileAdapter.OnMobileClickListener {

     private lateinit var topAdapter: LatestMobileAdapter
     private lateinit var binding: FragmentMyDevicesBinding
     private lateinit var restViewModel: RetroViewModel
     private var errorMessageShown = false
     private val TAG = "LTS_TAG"

     override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
          binding = FragmentMyDevicesBinding.inflate(inflater)
          return binding.root
     }

     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
          super.onViewCreated(view, savedInstanceState)
          topAdapter = LatestMobileAdapter(this)
          binding.rvLatest.apply {
               layoutManager = GridLayoutManager(requireContext(), 2)
               adapter = topAdapter }

          binding.shimmerLayout.stopShimmer()
          binding.shimmerLayout.visibility = GONE
          binding.rvLatest.visibility = VISIBLE


          topAdapter.submitList((activity as MainActivity).getMyMobiles())
          Log.i(TAG, "onViewCreated: ${(activity as MainActivity).getMyMobiles().size}")

          Handler(Looper.getMainLooper()).postDelayed(3000) {
               topAdapter.submitList((activity as MainActivity).getMyMobiles())

               Log.i(TAG, "onViewCreated: ${(activity as MainActivity).getMyMobiles().size}")
          }


          Handler(Looper.getMainLooper()).postDelayed(9000) {
               topAdapter.submitList((activity as MainActivity).getMyMobiles())
               Log.i(TAG, "onViewCreated: ${(activity as MainActivity).getMyMobiles().size}")
          }
     }


     override fun onPosClick(pos: Int) {
          val bundle = Bundle().apply { putString(Constants.PHONE_EXTRA, (activity as MainActivity).getMyMobiles()[pos].slug) }
          findNavController().navigate(R.id.actionToSecondaryHardware, bundle)
     }

     override fun onDetach() {
          super.onDetach()
          errorMessageShown = false
     }
//
//     private fun launchMyMobiles(mob: RoomPhoneModel) {
//          myMobiles.add(convertDataToPhone(mob.data, mob.slug))
//          (activity as MainActivity).getLocalUser().observe(viewLifecycleOwner) {
//               if (it?.mobileId != null && it.mobileId.isNotEmpty()) {
//                    it.mobileId.remove(mob.slug)
//                    onMobileListDerived(it.mobileId)
//               }
//          }
//     }


//
//    private fun onMobileListDerived(mobileIdList: ArrayList<String>) {
//         for (i in mobileIdList) {
//              if (!checkIfUrlExistsInPhones(i, myMobiles)) {
//                   postNewMobiles(i)
//              }
//         }
//         binding.shimmerLayout.stopShimmer()
//         binding.shimmerLayout.visibility = GONE
//         binding.rvLatest.visibility = VISIBLE
//    }
//
//
//    private fun postNewMobiles(url: String) {
//        val viewModel = ViewModelProvider(requireActivity())[RetroViewModel::class.java]
//        viewModel.getMobile(url)
//
//        viewModel.getResponse.observe(viewLifecycleOwner) {
//            if (it is Resource.Success) {
//                if (it.data?.data != null) {
//                    convertDataToPhone(it.data.data, url).apply {
//                         myMobiles.add(this)
//                         topAdapter.submitList(myMobiles)
//                         topAdapter.notifyDataSetChanged()
//                    }
//                }
//            }
//        }
//    }
}