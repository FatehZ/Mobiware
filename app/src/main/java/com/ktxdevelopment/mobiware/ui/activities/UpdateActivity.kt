package com.ktxdevelopment.mobiware.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
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


    }

    private fun clickListeners() {
        binding.btnLater.setOnClickListener {
            Intent(this, MainActivity::class.java).apply {
                putExtra(Constants.FIRST_RUN, checkIsFirstRun(this@UpdateActivity))
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }.also { startActivity(it) }
        }

        binding.btnUpdateMay.setOnClickListener { playStoreIntent(this) }
        binding.btnUpdateMust.setOnClickListener { playStoreIntent(this) }
    }

    private fun checkIsFirstRun(context: Context) : Boolean {
        Preferences.instantiate(context)
        return Preferences.getIsFirstRun(context)
    }
}