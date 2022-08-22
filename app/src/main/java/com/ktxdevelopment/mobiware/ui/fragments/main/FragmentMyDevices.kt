package com.ktxdevelopment.mobiware.ui.fragments.main

import android.os.Bundle
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
import com.ktxdevelopment.mobiware.clients.main.BaseClient.convertDataToPhone
import com.ktxdevelopment.mobiware.clients.main.PreferenceClient
import com.ktxdevelopment.mobiware.databinding.FragmentPhoneListBinding
import com.ktxdevelopment.mobiware.models.rest.Resource
import com.ktxdevelopment.mobiware.models.rest.search.Phone
import com.ktxdevelopment.mobiware.models.room.RoomPhoneModel
import com.ktxdevelopment.mobiware.ui.activities.MainActivity
import com.ktxdevelopment.mobiware.ui.recview.LatestMobileAdapter
import com.ktxdevelopment.mobiware.util.Constants
import com.ktxdevelopment.mobiware.viewmodel.RetroViewModel

class FragmentMyDevices : BaseFragment(), LatestMobileAdapter.OnMobileClickListener {

     private lateinit var topAdapter: LatestMobileAdapter
     private lateinit var binding: FragmentPhoneListBinding
     private lateinit var restViewModel: RetroViewModel
     private var errorMessageShown = false
     private lateinit var myMobiles: MutableLiveData<ArrayList<Phone>>

     override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
          binding = FragmentPhoneListBinding.inflate(inflater)
          return binding.root
     }

     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
          super.onViewCreated(view, savedInstanceState)
          initializeUI()
          retrieveMyDevices()
     }

     private fun initializeUI() {
          setActionBarTitle(getString(R.string.my_devices))
          restViewModel = ViewModelProvider(requireActivity())[RetroViewModel::class.java]
          topAdapter = LatestMobileAdapter(this)
          myMobiles = MutableLiveData(arrayListOf())
          binding.rvLatest.apply {
               layoutManager = GridLayoutManager(requireContext(), 2)
               adapter = topAdapter }
     }

     private fun retrieveMyDevices(){
          (activity as MainActivity).getMobile().observe(viewLifecycleOwner) { if (it != null) launchMyMobiles(it) }
          myMobiles.observe(viewLifecycleOwner) { if (it.isNotEmpty()) topAdapter.submitList(it) }
     }


     override fun onPosClick(pos: Int) {
          if (myMobiles.value!![pos].slug != PreferenceClient.getCurrentPhoneSlug(context!!))
               findNavController().navigate(
                    R.id.action_fragmentMyDevices_to_fragmentSecondaryHardware,
                    Bundle().apply { putString(Constants.PHONE_EXTRA, myMobiles.value!![pos].detail) }
               )
          else findNavController().navigate(R.id.actionToHardware)
     }

     override fun onDetach() {
          super.onDetach()
          errorMessageShown = false
     }

     private fun launchMyMobiles(mob: RoomPhoneModel) {
          myMobiles.value!!.add(convertDataToPhone(mob.data, mob.slug))
          (activity as MainActivity).getLocalUser().observe(viewLifecycleOwner) {
               if (it?.mobileId != null && it.mobileId.isNotEmpty()) {
                    it.mobileId.remove(mob.slug)
                    onMobileListDerived(it.mobileId)
               }
          }
     }


     private fun onMobileListDerived(mobileIdList: ArrayList<String>) {
          if (mobileIdList.size == 0) mainLayoutVisible()
          else postNewMobiles(mobileIdList)
     }


     private fun postNewMobiles(urlList: ArrayList<String>) {

          for (url in urlList) {
               val viewModel = ViewModelProvider(requireActivity())[RetroViewModel::class.java]
               viewModel.getMyDevices(url)
               viewModel.getMyDeviceResponse.observe(viewLifecycleOwner) {
                    if (it is Resource.Success) {
                         if (it.data?.data != null) {
                              convertDataToPhone(it.data.data, url).apply {
                                   myMobiles.value!!.add(this)
                                   if (urlList.indexOf(url) == (urlList.size - 1)) mainLayoutVisible()
                              }
                         }
                    }
               }
          }
     }

     private fun mainLayoutVisible() {
          binding.shimmerLayout.stopShimmer()
          binding.shimmerLayout.visibility = GONE
          binding.rvLatest.visibility = VISIBLE
     }
}