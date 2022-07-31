package com.ktxdevelopment.mobiware.ui.fragments.main

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.postDelayed
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.ktxdevelopment.mobiware.R
import com.ktxdevelopment.mobiware.databinding.FragmentLatestBinding
import com.ktxdevelopment.mobiware.models.rest.Resource
import com.ktxdevelopment.mobiware.models.rest.search.Phone
import com.ktxdevelopment.mobiware.ui.activities.BaseActivity
import com.ktxdevelopment.mobiware.ui.recview.LatestMobileAdapter
import com.ktxdevelopment.mobiware.util.Constants
import com.ktxdevelopment.mobiware.viewmodel.RetroViewModel


class FragmentLatest : BaseFragment(), LatestMobileAdapter.OnMobileClickListener {

    private lateinit var topAdapter: LatestMobileAdapter
    private lateinit var binding: FragmentLatestBinding
    private lateinit var restViewModel: RetroViewModel
    private var errorMessageShown = false
    private lateinit var myMobiles: MutableLiveData<ArrayList<Phone>>
    private val TAG = "LTS_TAG"


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        topAdapter = LatestMobileAdapter(this)
        restViewModel = ViewModelProvider(requireActivity())[RetroViewModel::class.java]
        myMobiles = MutableLiveData()

        binding.rvLatest.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = topAdapter }

        restViewModel.searchLatest()
        restViewModel.searchResponse.observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {
                    binding.shimmerLayout.visibility = GONE
                    binding.shimmerLayout.stopShimmer()
                    binding.rvLatest.visibility = VISIBLE
                    topAdapter.submitList(it.data!!.data.phones)
                }
                is Resource.Error -> {
                    Handler(Looper.getMainLooper()).postDelayed(500) { searchAgain() }
                }
                else -> {}
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentLatestBinding.inflate(inflater)
        return binding.root
    }


    private fun searchAgain() {
        restViewModel.searchLatest()
    }

    override fun onPosClick(pos: Int) {
        val bundle = Bundle().apply {
            putString(Constants.PHONE_EXTRA, topAdapter.currentList[pos].detail)
        }
        findNavController().navigate(R.id.actionToSecondaryHardware, bundle)
    }

    override fun onDetach() {
        super.onDetach()
        errorMessageShown = false
    }
}
