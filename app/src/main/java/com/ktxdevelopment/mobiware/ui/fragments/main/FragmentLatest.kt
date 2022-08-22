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
import com.ktxdevelopment.mobiware.databinding.FragmentPhoneListBinding
import com.ktxdevelopment.mobiware.models.rest.Resource
import com.ktxdevelopment.mobiware.ui.recview.LatestMobileAdapter
import com.ktxdevelopment.mobiware.util.Constants
import com.ktxdevelopment.mobiware.viewmodel.RetroViewModel


class FragmentLatest : BaseFragment(), LatestMobileAdapter.OnMobileClickListener {

    private lateinit var latestMobileAdapter: LatestMobileAdapter
    private lateinit var binding: FragmentPhoneListBinding
    private lateinit var restViewModel: RetroViewModel
    private var errorMessageShown = false


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeUI()
        retrieveLatestDevices()
    }

    private fun retrieveLatestDevices() {
        restViewModel.searchLatest()
        restViewModel.searchResponse.observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {
                    binding.shimmerLayout.visibility = GONE
                    binding.shimmerLayout.stopShimmer()
                    binding.rvLatest.visibility = VISIBLE
                    latestMobileAdapter.submitList(it.data!!.data.phones)
                }
                is Resource.Error -> {
                    Handler(Looper.getMainLooper()).postDelayed(500) { searchAgain() }
                }
                else -> {}
            }
        }
    }

    private fun initializeUI() {
        setActionBarTitle(getString(R.string.latest_devices))
        latestMobileAdapter = LatestMobileAdapter(this)
        restViewModel = ViewModelProvider(requireActivity())[RetroViewModel::class.java]

        binding.rvLatest.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = latestMobileAdapter }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentPhoneListBinding.inflate(inflater)
        return binding.root
    }


    private fun searchAgain() {
        restViewModel.searchLatest()
    }

    override fun onPosClick(pos: Int) {
        val bundle = Bundle().apply { putString(Constants.PHONE_EXTRA, latestMobileAdapter.currentList[pos].slug) }
        findNavController().navigate(R.id.action_fragmentLatest_to_fragmentSecondaryHardware, bundle)
    }

    override fun onDetach() {
        super.onDetach()
        errorMessageShown = false
    }
}
