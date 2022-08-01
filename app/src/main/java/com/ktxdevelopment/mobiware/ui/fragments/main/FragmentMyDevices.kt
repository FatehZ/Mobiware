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
import com.ktxdevelopment.mobiware.clients.BaseClient
import com.ktxdevelopment.mobiware.clients.BaseClient.checkIfUrlExistsInPhones
import com.ktxdevelopment.mobiware.clients.BaseClient.convertDataToPhone
import com.ktxdevelopment.mobiware.databinding.FragmentMyDevicesBinding
import com.ktxdevelopment.mobiware.models.rest.Resource
import com.ktxdevelopment.mobiware.models.rest.search.Phone
import com.ktxdevelopment.mobiware.ui.activities.MainActivity
import com.ktxdevelopment.mobiware.ui.recview.LatestMobileAdapter
import com.ktxdevelopment.mobiware.ui.recview.LatestMobileAdapter.OnMobileClickListener
import com.ktxdevelopment.mobiware.util.Constants
import com.ktxdevelopment.mobiware.viewmodel.RetroViewModel

class FragmentMyDevices: BaseFragment(), OnMobileClickListener {

     private lateinit var topAdapter: LatestMobileAdapter
     private lateinit var binding: FragmentMyDevicesBinding
     private lateinit var restViewModel: RetroViewModel
     private var errorMessageShown = false
     private lateinit var myMobiles: MutableLiveData<ArrayList<Phone>>
     private val TAG = "LTS_TAG"

     override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
          binding = FragmentMyDevicesBinding.inflate(inflater)
          topAdapter = LatestMobileAdapter(this)
          myMobiles = MutableLiveData(ArrayList())
          restViewModel = ViewModelProvider(requireActivity())[RetroViewModel::class.java]
          binding.rvLatest.apply {
               layoutManager = GridLayoutManager(requireContext(), 2)
               adapter = topAdapter }
          return binding.root
     }

     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
          super.onViewCreated(view, savedInstanceState)

//          (activity as MainActivity).getMobile().observe(viewLifecycleOwner) {
//               if (it != null) {
//                    myMobiles.value = myMobiles.value.add(convertDataToPhone(it))
//               }
//          }

          (activity as MainActivity).getLocalUser().observe(viewLifecycleOwner) {
               if (it?.mobileId != null && it.mobileId.isNotEmpty()) {
                    onMobileListDerived(it.mobileId)
               }
          }



          myMobiles.observe(viewLifecycleOwner) {
               topAdapter.submitList(it)
               binding.shimmerLayout.stopShimmer()
               binding.shimmerLayout.visibility = GONE
               binding.rvLatest.visibility = VISIBLE
          }
     }


     override fun onPosClick(pos: Int) {
          val bundle = Bundle().apply { putString(Constants.PHONE_EXTRA, myMobiles.value!![pos].detail) }
          findNavController().navigate(R.id.actionToSecondaryHardware, bundle)
     }

     override fun onDetach() {
          super.onDetach()
          errorMessageShown = false
     }














    private fun onMobileListDerived(mobileIdList: List<String>) {
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
        viewModel.getMobile(url)

        viewModel.getResponse.observe(viewLifecycleOwner) {
            if (it is Resource.Success) {
                if (it.data?.data != null) {
                    val vl = myMobiles.value
                    convertDataToPhone(it.data.data, url).apply {
                        if (!BaseClient.checkIfPhoneExistsInList(this, vl!!)) {
                            vl.add(this)
                            topAdapter.notifyDataSetChanged()
                        }
                    }
                    myMobiles.postValue(vl)
                }
            }
        }
    }
}