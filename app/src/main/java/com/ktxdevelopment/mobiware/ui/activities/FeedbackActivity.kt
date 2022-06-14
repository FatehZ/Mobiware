package com.ktxdevelopment.mobiware.ui.activities

import android.os.Bundle
import com.ktxdevelopment.mobiware.databinding.ActivityFeedbackBinding

class FeedbackActivity : BaseActivity() {

    private lateinit var binding: ActivityFeedbackBinding
    val TAG = "FD_TAG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSendFeedback.setOnClickListener {
            // TODO
        }

        setSupportActionBar(binding.toolbarFeedback)
        binding.toolbarFeedback.setNavigationOnClickListener { finish() }
    }
}