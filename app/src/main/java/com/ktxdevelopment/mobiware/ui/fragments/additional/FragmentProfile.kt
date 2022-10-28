package com.ktxdevelopment.mobiware.ui.fragments.additional

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.ktxdevelopment.mobiware.R
import com.ktxdevelopment.mobiware.clients.firebase.FirebaseClient
import com.ktxdevelopment.mobiware.clients.main.BaseClient.convertFireToLocalUser
import com.ktxdevelopment.mobiware.clients.main.BaseClient.convertLocalToFireUser
import com.ktxdevelopment.mobiware.clients.main.BaseClient.hasInternetConnection
import com.ktxdevelopment.mobiware.clients.main.PermissionClient
import com.ktxdevelopment.mobiware.clients.main.TextInputClient
import com.ktxdevelopment.mobiware.databinding.FragmentProfileBinding
import com.ktxdevelopment.mobiware.models.local.LocalUser
import com.ktxdevelopment.mobiware.ui.activities.AdditionalActivity
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
     private var previousImageUrlToDelete = ""


     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
          super.onViewCreated(view, savedInstanceState)
          loadLocalAndOnlineData()
          initializeUI()
     }

     private fun loadLocalAndOnlineData() {
          setActionBarTitle(getString(R.string.profile))
          tryEr {
               roomViewModel = ViewModelProvider(this)[LocalViewModel::class.java]
               roomViewModel.getLocalUser(requireContext())
               roomViewModel.localUser.observe(viewLifecycleOwner) {
                    userDetails = it
                    setUserDataInUI(it)
                    tryEr { if (hasInternetConnection(requireContext())) FirebaseClient.loadUserData(this) }
               }
          }
     }

     private fun initializeUI() {
          binding.civProfile.setOnClickListener {
               tryEr {
                    if (Build.VERSION.SDK_INT >= 32) showImageChooser()
                    else if (PermissionClient.hasGalleryPermissions(requireContext())) showImageChooser()
                    else ActivityCompat.requestPermissions(
                              requireActivity(),
                              arrayOf(READ_EXTERNAL_STORAGE),
                              READ_STORAGE_CODE
                         )
               }
          }


          binding.etUsernameProfile.filters = arrayOf(
               InputFilter { cs, _, _, _, _, _ ->
                    if (cs == "") return@InputFilter cs
                    if (cs.toString().matches(("[a-zA-Z ]+").toRegex())) cs
                    else ""
               }, InputFilter.LengthFilter(20)
          )

          binding.btnProfileUpdate.setOnClickListener { updateUserProfileData() }

          binding.etEmailProfile.setOnClickListener { tryEr { showErrorSnackbar(getString(R.string.email_cannot_change)) } }
     }


     private fun showImageChooser() {
//          val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//          galleryResultLauncher.launch(galleryIntent)
//          (activity as AdditionalActivity).startCrop()
          startCrop()
     }


     private val TAG = "L_TAG"

     private val cropImage = registerForActivityResult(CropImageContract()) { result ->
          if (result.isSuccessful) {
               mSelectedPhotoUri = result.uriContent

               Glide.with(this)
                    .load(mSelectedPhotoUri)
                    .centerCrop()
                    .placeholder(R.drawable.ic_account)
                    .into(binding.civProfile)

          } else {
               Log.i(TAG, " error mes: ${result.error?.message}")
               Log.i(TAG, " error loc : ${result.error?.localizedMessage}")
               Log.i(TAG, " error : ${result.error}")
               showErrorSnackbar(getString(R.string.smth_went_wrong))
          }
     }

     private fun startCrop() {

          val myOptions = CropImageOptions().apply {
               aspectRatioX = 1
               aspectRatioY = 1
               fixAspectRatio = true
               toolbarBackButtonColor = Color.CYAN
               guidelines = CropImageView.Guidelines.ON
               outputCompressFormat = Bitmap.CompressFormat.JPEG
               cropShape = CropImageView.CropShape.RECTANGLE
               activityTitle = "Crop Profile image"
               this.imageSourceIncludeGallery = true
               this.imageSourceIncludeCamera = true
               this.guidelinesColor = Color.GREEN
          }

          cropImage.launch(CropImageContractOptions(mSelectedPhotoUri, myOptions))
     }


//     private val galleryResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//               if (result.resultCode == AppCompatActivity.RESULT_OK)
//                    result.data?.let {
//                         try {
//                              mSelectedPhotoUri = result.data!!.data
//
//                              Glide.with(this)
//                                   .load(mSelectedPhotoUri)
//                                   .centerCrop()
//                                   .placeholder(R.drawable.ic_account)
//                                   .into(binding.civProfile)
//                         } catch (e: Exception) {
//                              showErrorSnackbar(getString(R.string.smth_went_wrong))
//                         }
//                    }
//          }

     private fun setUserDataInUI(user: LocalUser) {
          tryEr {
               Glide
                    .with(this)
                    .load(PermissionClient.getBitmapFromBase64(user.image64))
                    .placeholder(R.drawable.ic_account_big)
                    .centerCrop()
                    .into(binding.civProfile)
          }


          user.username.let { if (it.isNotEmpty()) binding.etUsernameProfile.setText(it) }
          user.email.let { if (it.isNotEmpty()) binding.etEmailProfile.setText(it) }

          if (user.mobileNumberBase.isNotEmpty()) binding.etMobileNumberProfile.setText(user.mobileNumberBase)
          if (user.mobileCountryCode.isNotEmpty()) binding.codePicker.setCountryForPhoneCode(
               user.mobileCountryCode.toInt()
          )
     }

     private fun uploadUserImageThenFetch(userUpdated: HashMap<String, Any>) {
          showProgressDialogCancellable()
          previousImageUrlToDelete = userDetails.imageOnline
          if (mSelectedPhotoUri != null) {
               tryEr {
                    val sRef: StorageReference =
                         FirebaseStorage.getInstance().getReference("profile_images").child(
                              "USER_IMAGE_" + System.currentTimeMillis() + "." + PermissionClient.getFileExtension(
                                   requireActivity(),
                                   mSelectedPhotoUri
                              )
                         )

                    sRef.putFile(mSelectedPhotoUri!!).addOnSuccessListener { taskSnapshot ->
                         taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
                              mProfileImageOnlineDBUri = uri.toString()
                              userDetails.image64 =
                                   PermissionClient.getBaseImageFromString(mProfileImageOnlineDBUri)
                              userUpdated[Constants.IMAGE_ONLINE] = mProfileImageOnlineDBUri
                              fetchUpdatedUserToDB(userUpdated)
                              roomViewModel.deleteUnusedUserProfileImageFromFirestore(
                                   requireContext(),
                                   previousImageUrlToDelete
                              )
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

          if (TextInputClient.validateFilledInput(binding.etUsernameProfile.text.toString()) && binding.etUsernameProfile.text.toString() != userDetails.username) {
               anyChangesMade = true
               if (TextInputClient.validateUsername(binding.etUsernameProfile)) {
                    userUpdated[Constants.USERNAME] = binding.etUsernameProfile.text.toString()
               } else errorPresent = true
          }

          if (TextInputClient.validateFilledInput(binding.etMobileNumberProfile.text.toString())) {
               if (binding.etMobileNumberProfile.text.toString() != userDetails.mobileNumberBase ||
                    binding.codePicker.selectedCountryCode != userDetails.mobileCountryCode
               ) anyChangesMade = true

               if (TextInputClient.validateMobileNumber(binding.etMobileNumberProfile)) {
                    userUpdated[Constants.MOBILE_NUMBER_BASE] =
                         binding.etMobileNumberProfile.text.toString()
                    userUpdated[Constants.MOBILE_NUMBER_CODE] =
                         binding.codePicker.selectedCountryCode
               } else errorPresent = true
          }

          if (mSelectedPhotoUri != null) anyChangesMade = true

          if (!errorPresent) {
               if (anyChangesMade) {
                    tryEr {
                         if (hasInternetConnection(requireContext())) {

                              if (mSelectedPhotoUri != null) uploadUserImageThenFetch(userUpdated)
                              else fetchUpdatedUserToDB(userUpdated)

                         } else showErrorSnackbar(getString(R.string.no_connection_error))
                    }
               } else showErrorSnackbar(getString(R.string.no_profile_detail_change))
          }
     }

     private fun fetchUpdatedUserToDB(updatedUser: HashMap<String, Any>) {
          showProgressDialogCancellable()
          FirebaseClient.updateUserProfileData(this, updatedUser)
     }

     fun onProfileLoadOnlineSuccess(user: LocalUser) {
          tryEr {
               if (userDetails.imageOnline != user.imageOnline) userDetails =
                    convertFireToLocalUser(user)
               setUserDataInUI(userDetails)
               mProfileImageOnlineDBUri = ""
               mSelectedPhotoUri = null
               roomViewModel.writeUserToPreferences(
                    requireContext(),
                    convertLocalToFireUser(user)
               )
          }
     }

     fun onProfileUpdateSuccess() {
          mProfileImageOnlineDBUri = ""
          mSelectedPhotoUri = null
          hideProgressDialog()
          tryEr {
               roomViewModel.writeUserToPreferences(
                    requireContext(),
                    convertLocalToFireUser(userDetails)
               )
          }
     }

     fun onProfileUpdateFailure() {
          hideProgressDialog()
          shortToast(R.string.smth_went_wrong)
     }

     override fun onCreateView(
          inflater: LayoutInflater,
          container: ViewGroup?,
          savedInstanceState: Bundle?
     ): View {
          binding = FragmentProfileBinding.inflate(inflater, container, false)
          return binding.root
     }


//     fun customSquareImage(sourceUri: Uri, destinationUri: Uri) {
//          UCrop.of(sourceUri, destinationUri)
//               .withAspectRatio(1.0F, 1.0F)
//               .withMaxResultSize(400, 400)
//               .start(requireActivity())
//     }
}