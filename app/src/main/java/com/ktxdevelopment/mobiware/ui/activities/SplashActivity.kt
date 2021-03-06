package com.ktxdevelopment.mobiware.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.os.postDelayed
import com.ktxdevelopment.mobiware.R
import com.ktxdevelopment.mobiware.clients.RemoteClient
import com.ktxdevelopment.mobiware.clients.firebase.FirebaseClient.getCurrentUserId
import com.ktxdevelopment.mobiware.databinding.ActivitySplashBinding
import com.ktxdevelopment.mobiware.util.Constants

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val handler = Handler(Looper.getMainLooper())
    private var duration = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) duration = 1500L

        checkForUpdate()

    }

    private fun mainIntent() = handler.apply {
        postDelayed(duration) {
            Intent(this@SplashActivity, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP and Intent.FLAG_ACTIVITY_NEW_TASK)
            }.also { startActivity(it) }
            finish()
        }
    }

    private fun signUpIntent() {
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        handler.apply {
            postDelayed(duration) {
                Intent(this@SplashActivity, SignUpActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP and Intent.FLAG_ACTIVITY_NEW_TASK)
                }.also { startActivity(it) }
                finish()
            }
        }
    }

    private fun updateIntent(sign: String) = handler.apply {
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            postDelayed(duration) {
                Intent(this@SplashActivity, UpdateActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP and Intent.FLAG_ACTIVITY_NEW_TASK)
                    putExtra(Constants.UPDATE_SIGN, sign)
                }.also { startActivity(it) }
                finish()
            }
        }


    private fun checkForUpdate() {
        val map = RemoteClient.checkMustUpdate(this)
        if (map[Constants.ANY_UPDATE] as Boolean) updateIntent(map[Constants.UPDATE_SIGN] as String)
        else if (getCurrentUserId().isEmpty()) signUpIntent()
        else mainIntent()
    }
}