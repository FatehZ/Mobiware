package com.ktxdevelopment.mobiware.ui.fragments.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.ktxdevelopment.mobiware.R
import com.ktxdevelopment.mobiware.clients.main.BaseClient.checkIfUrlExistsInPhones
import com.ktxdevelopment.mobiware.clients.main.BaseClient.convertDataToPhone
import com.ktxdevelopment.mobiware.clients.main.PreferenceClient
import com.ktxdevelopment.mobiware.databinding.FragmentMyDevicesBinding
import com.ktxdevelopment.mobiware.models.rest.Resource
import com.ktxdevelopment.mobiware.models.rest.search.Phone
import com.ktxdevelopment.mobiware.models.room.RoomPhoneModel
import com.ktxdevelopment.mobiware.ui.activities.MainActivity
import com.ktxdevelopment.mobiware.ui.recview.LatestMobileAdapter
import com.ktxdevelopment.mobiware.util.Constants
import com.ktxdevelopment.mobiware.viewmodel.RetroViewModel

class FragmentMyDevices: BaseFragment(), LatestMobileAdapter.OnMobileClickListener{

     private lateinit var topAdapter: LatestMobileAdapter
     private lateinit var binding: FragmentMyDevicesBinding
     private lateinit var restViewModel: RetroViewModel
     private var errorMessageShown = false
     private lateinit var myMobiles: MutableLiveData<ArrayList<Phone>>
     private val TAG = "LTS_TAG"

     override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
          binding = FragmentMyDevicesBinding.inflate(inflater)
          return binding.root
     }

     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
          super.onViewCreated(view, savedInstanceState)
          restViewModel = ViewModelProvider(requireActivity())[RetroViewModel::class.java]
          topAdapter = LatestMobileAdapter(this)
          binding.rvLatest.apply {
               layoutManager = GridLayoutManager(requireContext(), 2)
               adapter = topAdapter }

          binding.shimmerLayout.stopShimmer()
          binding.shimmerLayout.visibility = GONE
          binding.rvLatest.visibility = VISIBLE

          (activity as MainActivity).getMobile().observe(viewLifecycleOwner) {
               if (it != null) {
                    launchMyMobiles(it)
               }
          }

          myMobiles = MutableLiveData()
          myMobiles.observe(viewLifecycleOwner) {
               if (it.isNotEmpty()) {
                    topAdapter.submitList(it)
               }
          }
     }


     override fun onPosClick(pos: Int) {
          if (myMobiles.value!![pos].slug != PreferenceClient.getCurrentPhoneSlug(context!!)) {
               Log.i(TAG, "onPosClick: ${PreferenceClient.getCurrentPhoneSlug(context!!)}")
               Log.i(TAG, "onPosClick: ${myMobiles.value!![pos].slug}")
               val bundle = Bundle().apply { putString(Constants.PHONE_EXTRA, myMobiles.value!![pos].detail) }
               findNavController().navigate(R.id.actionToSecondaryHardware, bundle)
          }else{
               findNavController().navigate(R.id.actionToHardware)
          }
     }

     override fun onDetach() {
          super.onDetach()
          errorMessageShown = false
     }

     private fun launchMyMobiles(mob: RoomPhoneModel) {
          myMobiles.value = arrayListOf(convertDataToPhone(mob.data, mob.slug))
          (activity as MainActivity).getLocalUser().observe(viewLifecycleOwner) {
               if (it?.mobileId != null && it.mobileId.isNotEmpty()) {
                    it.mobileId.remove(mob.slug)
                    onMobileListDerived(it.mobileId)
               }
          }
     }



     private fun onMobileListDerived(mobileIdList: ArrayList<String>) {
          Log.i(TAG, "mobile size : ${mobileIdList.size}")
          for (i in mobileIdList) {
               Log.i(TAG, "onMobileListDerived: $i")
               if (!checkIfUrlExistsInPhones(i, myMobiles.value!!)) {
                    postNewMobiles(i)
               }
          }
     }


     private fun postNewMobiles(url: String) {
          val viewModel = ViewModelProvider(requireActivity())[RetroViewModel::class.java]
          viewModel.getMyDevices(url)

          viewModel.getMyDeviceResponse.observe(viewLifecycleOwner) {
               if (it is Resource.Success) {
                    if (it.data?.data != null) {
                         convertDataToPhone(it.data.data, url).apply {
                              myMobiles.value?.add(this)
                         }
                    }
               }
          }
     }
}