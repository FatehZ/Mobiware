package com.ktxdevelopment.mobiware.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.ktxdevelopment.mobiware.R
import com.ktxdevelopment.mobiware.clients.BaseClient.playStoreIntent
import com.ktxdevelopment.mobiware.databinding.ActivityUpdateBinding
import com.ktxdevelopment.mobiware.util.Constants
import com.ktxdevelopment.mobiware.clients.Preferences

class UpdateActivity : BaseActivity() {

    private lateinit var binding: ActivityUpdateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        clickListeners()

        if (intent.hasExtra(Constants.UPDATE_SIGN)) {
            if (intent.getStringExtra(Constants.UPDATE_SIGN) == Constants.MUST) {
                mustLayoutVisible()
            }else{
                mayLayoutVisible()
            }
        }
    }

    private fun clickListeners() {
        binding.btnLater.setOnClickListener {
            Intent(this, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }.also { startActivity(it) }
        }

        binding.btnUpdateMay.setOnClickListener { playStoreIntent(this) }
        binding.btnUpdateMust.setOnClickListener { playStoreIntent(this) }
    }

    private fun mayLayoutVisible() {
        binding.llMay.visibility = View.VISIBLE
        binding.llMust.visibility = View.GONE
    }

    private fun mustLayoutVisible() {
        binding.llMay.visibility = View.GONE
        binding.llMust.visibility = View.VISIBLE
    }
}