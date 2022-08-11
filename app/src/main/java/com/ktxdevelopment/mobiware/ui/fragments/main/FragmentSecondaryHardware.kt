package com.ktxdevelopment.mobiware.ui.fragments.main

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.ktxdevelopment.mobiware.R
import com.ktxdevelopment.mobiware.clients.firebase.ListUtilClient.getDeviceModelLogo
import com.ktxdevelopment.mobiware.databinding.FragmentSecondaryHardwareBinding
import com.ktxdevelopment.mobiware.models.rest.Resource
import com.ktxdevelopment.mobiware.models.rest.product.Data
import com.ktxdevelopment.mobiware.ui.recview.MobileSpecsAdapter
import com.ktxdevelopment.mobiware.util.Constants
import com.ktxdevelopment.mobiware.viewmodel.RetroViewModel


class FragmentSecondaryHardware : Fragment() {
     private var binding: FragmentSecondaryHardwareBinding? = null
     private lateinit var mobile: Data
     private lateinit var viewModel: RetroViewModel
     private val TAG = "HRD_TAG"

     private var rPlatform: MobileSpecsAdapter? = null
     private var rDisplay: MobileSpecsAdapter? = null
     private var rMemory: MobileSpecsAdapter? = null
     private var rBody: MobileSpecsAdapter? = null
     private var rLaunch: MobileSpecsAdapter? = null
     private var rComms: MobileSpecsAdapter? = null
     private var rBattery: MobileSpecsAdapter? = null
     private var rMainCam: MobileSpecsAdapter? = null
     private var rSelfCam: MobileSpecsAdapter? = null


     override fun onCreateView(
          inflater: LayoutInflater,
          container: ViewGroup?,
          savedInstanceState: Bundle?
     ): View {
          if (binding == null) {
               binding = FragmentSecondaryHardwareBinding.inflate(layoutInflater)
          }
          return binding!!.root
     }


     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
          super.onViewCreated(view, savedInstanceState)

          if (binding == null) binding = FragmentSecondaryHardwareBinding.inflate(layoutInflater)

          viewModel = ViewModelProvider(requireActivity())[RetroViewModel::class.java]
          loadShimmerVisible()
          setupRecyclerViews()
          if (arguments?.getString(Constants.PHONE_EXTRA) != null) {
               viewModel.getMobile(arguments!!.getString(Constants.PHONE_EXTRA)!!)
               viewModel.getResponse.observe(requireActivity()) { res ->
                    if (res is Resource.Success && res.data != null) {
                         checkThenSetDataInUI(res.data.data)
                         loadContentVisible()
                    }
               }
          }
     }

     private fun checkThenSetDataInUI(data: Data) {
          try {
               setDataInUI(data)
          } catch (e: Exception) {
          }
     }

     private fun setDataInUI(data: Data) {
          mobile = data
          Glide.with(requireActivity())
               .load(mobile.thumbnail)
               .placeholder(R.color.white)
               .into(binding!!.ivMainPhone)

          Glide.with(requireActivity())
               .load(getDeviceModelLogo(mobile.brand))
               .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                         e: GlideException?,
                         model: Any?,
                         target: Target<Drawable>?,
                         isFirstResource: Boolean
                    ): Boolean {
                         binding!!.ivMainLogo.visibility = View.GONE
                         binding!!.tvMainModel.text = (data.brand + " " + data.phone_name)
                         return false
                    }

                    override fun onResourceReady(
                         resource: Drawable?,
                         model: Any?,
                         target: Target<Drawable>?,
                         dataSource: DataSource?,
                         isFirstResource: Boolean
                    ): Boolean {
                         binding!!.tvMainModel.text = (data.phone_name)
                         return false
                    }
               })
               .placeholder(R.color.white)
               .into(binding!!.ivMainLogo)


          for (i in data.specifications[4].specs) {
               if (i.key.lowercase() == Constants.CHIPSET) {
                    binding!!.tvMainCPU.text = (i.`val`[0])
                    break
               }
          }

          for (i in data.specifications[3].specs) {
               if (i.key.lowercase() == Constants.DISPLAY) {
                    binding!!.tvMainCPU.text = (i.`val`[0])
                    break
               }
          }

          binding!!.tvMainOS.text = (data.os)
          binding!!.tvMainDisplay.text = (data.specifications[3].specs[0].`val`[0])

          submitRecyclerViewItems(data)
     }

     private fun setupRecyclerViews() {

          rPlatform = MobileSpecsAdapter()
          rMemory = MobileSpecsAdapter()
          rDisplay = MobileSpecsAdapter()
          rBody = MobileSpecsAdapter()
          rLaunch = MobileSpecsAdapter()
          rComms = MobileSpecsAdapter()
          rBattery = MobileSpecsAdapter()
          rMainCam = MobileSpecsAdapter()
          rSelfCam = MobileSpecsAdapter()


          binding!!.rvMainPlatform.apply {
               layoutManager = LinearLayoutManager(requireContext()); adapter = rPlatform
          }
          binding!!.rvMainDisplay.apply {
               layoutManager = LinearLayoutManager(requireContext()); adapter = rDisplay
          }
          binding!!.rvMainMemory.apply {
               layoutManager = LinearLayoutManager(requireContext()); adapter = rMemory
          }
          binding!!.rvMainBattery.apply {
               layoutManager = LinearLayoutManager(requireContext()); adapter = rBattery
          }
          binding!!.rvMainBody.apply {
               layoutManager = LinearLayoutManager(requireContext()); adapter = rBody
          }
          binding!!.rvMainComm.apply {
               layoutManager = LinearLayoutManager(requireContext()); adapter = rComms
          }
          binding!!.rvMainLaunch.apply {
               layoutManager = LinearLayoutManager(requireContext()); adapter = rLaunch
          }
          binding!!.rvMainMainCamera.apply {
               layoutManager = LinearLayoutManager(requireContext()); adapter = rMainCam
          }
          binding!!.rvMainSelfieCamera.apply {
               layoutManager = LinearLayoutManager(requireContext()); adapter = rSelfCam
          }
     }


     private fun loadContentVisible() {
          if (binding != null) {
               binding!!.mainLayout.visibility = View.VISIBLE
               binding!!.shimmerLayout.stopShimmer()
               binding!!.shimmerLayout.visibility = View.GONE
          }
     }

     private fun loadShimmerVisible() {
          binding!!.mainLayout.visibility = View.GONE
          binding!!.shimmerLayout.visibility = View.VISIBLE
          binding!!.shimmerLayout.startShimmer()
     }

     private fun submitRecyclerViewItems(data: Data) {
          rPlatform!!.submitList(data.specifications[4].specs)
          rDisplay!!.submitList(data.specifications[3].specs)
          rMemory!!.submitList(data.specifications[5].specs)
          rBattery!!.submitList(data.specifications[11].specs)
          rBody!!.submitList(data.specifications[2].specs)
          rLaunch!!.submitList(data.specifications[1].specs)
          rMainCam!!.submitList(data.specifications[6].specs)
          rSelfCam!!.submitList(data.specifications[7].specs)
          rComms!!.submitList(data.specifications[9].specs)
     }

     override fun onDestroy() {
          super.onDestroy()
          rPlatform = null
          rMemory = null
          rBattery = null
          rBody = null
          rLaunch = null
          rMainCam = null
          rSelfCam = null
     }

     override fun onDestroyView() {
          super.onDestroyView()
          binding = null
     }

     override fun onPrimaryNavigationFragmentChanged(isPrimaryNavigationFragment: Boolean) {
          super.onPrimaryNavigationFragmentChanged(isPrimaryNavigationFragment)
          binding = null
     }
}