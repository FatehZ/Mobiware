package com.ktxdevelopment.mobiware.ui.fragments.additional

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.ktxdevelopment.mobiware.R
import com.ktxdevelopment.mobiware.clients.main.BaseClient.convertFireToLocalUser
import com.ktxdevelopment.mobiware.clients.main.BaseClient.convertLocalToFireUser
import com.ktxdevelopment.mobiware.clients.main.BaseClient.hasInternetConnection
import com.ktxdevelopment.mobiware.clients.main.PermissionClient
import com.ktxdevelopment.mobiware.clients.main.TextInputClient
import com.ktxdevelopment.mobiware.clients.firebase.FirebaseClient
import com.ktxdevelopment.mobiware.databinding.FragmentProfileBinding
import com.ktxdevelopment.mobiware.models.local.LocalUser
import com.ktxdevelopment.mobiware.ui.fragments.main.BaseFragment
import com.ktxdevelopment.mobiware.util.Constants
import com.ktxdevelopment.mobiware.util.Constants.READ_STORAGE_CODE
import com.ktxdevelopment.mobiware.util.tryEr
import com.ktxdevelopment.mobiware.viewmodel.LocalViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentProfile : BaseFragment() {
     private lateinit var binding: FragmentProfileBinding
     private var mSelectedPhotoUri: Uri? = null
     private var mProfileImageOnlineDBUri: String = ""
     private lateinit var userDetails: LocalUser
     private lateinit var roomViewModel: LocalViewModel



     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
          super.onViewCreated(view, savedInstanceState)
          tryEr {
               roomViewModel = ViewModelProvider(this)[LocalViewModel::class.java]
               roomViewModel.getLocalUser(context!!)
               roomViewModel.localUser.observe(viewLifecycleOwner) {
                    userDetails = it
                    setUserDataInUI(it)
               }
          }

          tryEr { if (hasInternetConnection(context!!)) FirebaseClient.loadUserData(this) }

          binding.civProfile.setOnClickListener {
               tryEr {
                    if (PermissionClient.hasGalleryPermissions(context!!)) showImageChooser()
                    else { ActivityCompat.requestPermissions(activity!!, arrayOf(READ_EXTERNAL_STORAGE), READ_STORAGE_CODE) }
               }
          }


          binding.etUsernameProfile.filters = arrayOf(InputFilter { cs, _, _, _, _, _ ->
               if (cs == "") { return@InputFilter cs }
               if (cs.toString().matches(("[a-zA-Z ]+").toRegex())) { cs } else ""
          })

          binding.btnProfileUpdate.setOnClickListener {
               if (mSelectedPhotoUri != null) { uploadUserImage() }
               else { updateUserProfileData() }
          }

          binding.etEmailProfile.setOnClickListener {
               tryEr { showErrorSnackbar(getString(R.string.email_cannot_change)) }
          }

     }

     private fun showImageChooser() {
          val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
          galleryResultLauncher.launch(galleryIntent)
     }


     private val galleryResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
               if (result.resultCode == AppCompatActivity.RESULT_OK) {
                    result.data?.let {
                         try {
                              mSelectedPhotoUri = result.data!!.data
                              Glide.with(this)
                                   .load(mSelectedPhotoUri)
                                   .centerCrop()
                                   .placeholder(R.drawable.ic_account)
                                   .into(binding.civProfile)
                         } catch (e: Exception) {
                              showErrorSnackbar(getString(R.string.smth_went_wrong))
                         }
                    }
               }
          }

     private fun setUserDataInUI(user: LocalUser) {
          Glide
               .with(this)
               .load(PermissionClient.getBitmapFromBase64(user.image64))
               .placeholder(R.drawable.ic_account_big)
               .centerCrop()
               .into(binding.civProfile)

          user.username.let { if (it.isNotEmpty()) binding.etUsernameProfile.setText(it) }
          user.email.let { if (it.isNotEmpty()) binding.etEmailProfile.setText(it) }

          if (user.mobileNumberBase.isNotEmpty()) binding.etMobileNumberProfile.setText(user.mobileNumberBase)
          if (user.mobileCountryCode.isNotEmpty()) binding.codePicker.setCountryForPhoneCode(user.mobileCountryCode.toInt())
     }

     private fun uploadUserImage() {
          showProgressDialogCancellable()
          if (mSelectedPhotoUri != null) {
               tryEr {
                    val sRef: StorageReference = FirebaseStorage.getInstance().getReference("profile_images").child(
                              "USER_IMAGE_" + System.currentTimeMillis() + "." + PermissionClient.getFileExtension(activity!!, mSelectedPhotoUri))
                    sRef.putFile(mSelectedPhotoUri!!).addOnSuccessListener { taskSnapshot -> taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
                              mProfileImageOnlineDBUri = uri.toString()
                              userDetails.image64 = PermissionClient.getBaseImageFromString(mProfileImageOnlineDBUri)
                              updateUserProfileData()
                         }
                    }.addOnFailureListener {
                         showErrorSnackbar(getString(R.string.error_occurred))
                         hideProgressDialog()
                    }
               }
          }
     }

     private fun updateUserProfileData() {
          val userUpdated = HashMap<String, Any>()
          var anyChangesMade = false
          var errorPresent = false

          if (TextInputClient.validateFilledInput(mProfileImageOnlineDBUri)) {
               userUpdated[Constants.IMAGE_ONLINE] = mProfileImageOnlineDBUri
               anyChangesMade = true
          }

          if (TextInputClient.validateFilledInput(binding.etUsernameProfile.text.toString()) && binding.etUsernameProfile.text.toString() != userDetails.username) {
               anyChangesMade = true
               if (TextInputClient.validateUsername(binding.etUsernameProfile)) {
                    userUpdated[Constants.USERNAME] = binding.etUsernameProfile.text.toString()
               } else { errorPresent = true }
          }

          if (TextInputClient.validateFilledInput(binding.etMobileNumberProfile.text.toString())) {
               if (binding.etMobileNumberProfile.text.toString() != userDetails.mobileNumberBase ||
                    binding.codePicker.selectedCountryCode != userDetails.mobileCountryCode) { anyChangesMade = true }

               if (TextInputClient.validateMobileNumber(binding.etMobileNumberProfile)) {
                    userUpdated[Constants.MOBILE_NUMBER_BASE] = binding.etMobileNumberProfile.text.toString()
                    userUpdated[Constants.MOBILE_NUMBER_CODE] = binding.codePicker.selectedCountryCode
               } else { errorPresent = true }
          }

          if (!errorPresent) {
               if (anyChangesMade) {
                    tryEr {
                         if (hasInternetConnection(context!!)) {
                              showProgressDialogCancellable()
                              FirebaseClient.updateUserProfileData(this, userUpdated)
                         } else showErrorSnackbar(getString(R.string.no_connection_error))
                    }
               }else showErrorSnackbar(getString(R.string.no_profile_detail_change))
          }
     }

     fun onProfileLoadOnlineSuccess(user: LocalUser) {
          userDetails = convertFireToLocalUser(user)
          setUserDataInUI(userDetails)
          mProfileImageOnlineDBUri = ""
          mSelectedPhotoUri = null
          tryEr { roomViewModel.writeUserToPreferences(
               context!!,
               convertLocalToFireUser(user)
          ) }
     }

     fun onProfileUpdateSuccess() {
          mProfileImageOnlineDBUri = ""
          mSelectedPhotoUri = null
          hideProgressDialog()
          tryEr { roomViewModel.writeUserToPreferences(
               context!!,
               convertLocalToFireUser(userDetails),
          ) }
     }

     fun onProfileUpdateFailure() {
          hideProgressDialog()
          shortToast(R.string.smth_went_wrong) }

     override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
          binding = FragmentProfileBinding.inflate(inflater, container, false)
          return binding.root
     }

}