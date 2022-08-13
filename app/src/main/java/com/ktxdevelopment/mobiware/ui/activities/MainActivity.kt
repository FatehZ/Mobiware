package com.ktxdevelopment.mobiware.ui.activities

import android.app.UiModeManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ktxdevelopment.mobiware.R
import com.ktxdevelopment.mobiware.clients.firebase.FirebaseClient
import com.ktxdevelopment.mobiware.clients.main.BaseClient
import com.ktxdevelopment.mobiware.clients.main.PermissionClient
import com.ktxdevelopment.mobiware.clients.ui.MainActivityClient
import com.ktxdevelopment.mobiware.clients.ui.MainActivityClient.launchProfileIntent
import com.ktxdevelopment.mobiware.databinding.ActivityMainBinding
import com.ktxdevelopment.mobiware.databinding.NavHeaderBinding
import com.ktxdevelopment.mobiware.models.local.LocalUser
import com.ktxdevelopment.mobiware.models.room.RoomPhoneModel
import com.ktxdevelopment.mobiware.util.Constants
import com.ktxdevelopment.mobiware.util.tryEr
import com.ktxdevelopment.mobiware.viewmodel.LocalViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

     private lateinit var mainBinding: ActivityMainBinding
     private val TAG = "MAIN_TAG"
     private lateinit var viewModel: LocalViewModel
     private var mobile: MutableLiveData<RoomPhoneModel> = MutableLiveData()
     private var user: MutableLiveData<LocalUser> = MutableLiveData()

     override fun onCreate(savedInstanceState: Bundle?) {
          super.onCreate(savedInstanceState)

          Log.i(TAG, "onCreate: ${AppCompatDelegate.getDefaultNightMode()}")


          mainBinding = ActivityMainBinding.inflate(layoutInflater).also { setContentView(it.root) }
          setupPrimaryUI()
          viewModel = ViewModelProvider(this)[LocalViewModel::class.java]

          tryEr {
               if (intent.hasExtra(Constants.PHONE_EXTRA)) {
                    mobile.postValue(intent.getParcelableExtra(Constants.PHONE_EXTRA)!!)
                    user.postValue(BaseClient.convertFireToLocalUser(intent.getParcelableExtra(Constants.USER_EXTRA)!!))
               } else { getUserAndMobile() }

               user.observe(this) { if (it != null) setupNavUI(it) }
          }
          FirebaseClient.loadUserData(this)
     }

     override fun onResume() {
          super.onResume()
     }

     override fun onNavigationItemSelected(item: MenuItem): Boolean {
          return MainActivityClient.setUpMainNavigationClickListeners(item, this@MainActivity)
     }

     private fun setupPrimaryUI() {
          findNavController(R.id.navHost).navigate(R.id.actionToHardware)
          setSupportActionBar(mainBinding.toolbarMainActivity)
          supportActionBar?.setDisplayHomeAsUpEnabled(true)
          mainBinding.toolbarMainActivity.setNavigationOnClickListener {
               MainActivityClient.toggleDrawer(mainBinding)
          }
          mainBinding.navView.setNavigationItemSelectedListener(this)
          mainBinding.navView.getHeaderView(0).findViewById<ImageView>(R.id.ivNavProfileImage).setOnClickListener {
               closeDrawer()
               launchProfileIntent(this)
          }
          mainBinding.navView.getHeaderView(0).findViewById<TextView>(R.id.tvUserName).setOnClickListener {
               closeDrawer()
               launchProfileIntent(this)
          }
     }

     private fun getUserAndMobile() {
          viewModel.getLocalUser(this)
          viewModel.getRoomMobileDetails()

          viewModel.roomMobileDetails.observe(this) { us ->
               if (us != null) mobile.postValue(us)
          }

          viewModel.localUser.observe(this) {
               if (it != null) {
                    user.postValue(it)
               }
          }
     }

     private fun setupNavUI(user: LocalUser) {
          val headerBinding = NavHeaderBinding.bind(mainBinding.navView.getHeaderView(0))
          headerBinding.tvUserName.text = user.username
          Glide.with(this)
               .load(PermissionClient.getBitmapFromBase64(user.image64))
               .placeholder(R.drawable.ic_account)
               .into(headerBinding.ivNavProfileImage)

     }


     fun closeDrawer() { if (mainBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) mainBinding.drawerLayout.closeDrawer(GravityCompat.START) }

     fun getMobile() = mobile

     override fun onBackPressed() {
          MainActivityClient.onCustomBackPressed(this, mainBinding, findNavController(R.id.navHost))
     }


     fun onUserDataObtainedFromFirestore(mUser: LocalUser) {
          user.postValue(BaseClient.convertFireToLocalUser(mUser))
     }


     fun getLocalUser() = user
     fun getLocalViewModel() = viewModel

     override fun signOut() {
          hideProgressDialog()
          viewModel.clearDatabaseWithWork(this)
          (getSystemService(Context.UI_MODE_SERVICE) as UiModeManager).setApplicationNightMode(UiModeManager.MODE_NIGHT_AUTO)
          Firebase.auth.signOut()
          Intent(this, IntroductionActivity::class.java)
               .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TOP)
               .setAction("")
               .also { startActivity(it); finish() }
     }
}