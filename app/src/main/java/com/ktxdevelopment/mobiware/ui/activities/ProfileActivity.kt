package com.ktxdevelopment.mobiware.ui.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.text.isDigitsOnly
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.ktxdevelopment.mobiware.clients.PermissionClient
import com.ktxdevelopment.mobiware.clients.PermissionClient.getFileExtension
import com.ktxdevelopment.mobiware.clients.firebase.FirebaseClient
import com.ktxdevelopment.mobiware.databinding.ActivityProfileBinding
import com.ktxdevelopment.mobiware.models.firebase.FireUser
import com.ktxdevelopment.mobiware.util.Constants
import com.ktxdevelopment.mobiware.util.Constants.READ_STORAGE_CODE

class ProfileActivity : BaseActivity() {
    private lateinit var binding: ActivityProfileBinding
    private var mSelectedPhotoUri: Uri? = null
    private var mProfileImageOnlineDBUri: String = ""
    private lateinit var mUserDetails: FireUser


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FirebaseClient.loadUserData(this)

        binding.civProfile.setOnClickListener {
            if (PermissionClient.hasGalleryPermissions(this)) {
                showImageChooser()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_STORAGE_CODE
                )
            }
        }

        binding.toolbarProfile.setNavigationOnClickListener { finish() }

        binding.btnProfileUpdate.setOnClickListener {
            if (mSelectedPhotoUri != null) {
                uploadUserImage()
            } else {
                updateUserProfileData()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_STORAGE_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showImageChooser()
            } else {
                Toast.makeText(
                    this,
                    "You denied the permission for storage. You can change it from app settings",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showImageChooser() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryResultLauncher.launch(galleryIntent)
    }

    private val galleryResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let {
                    mSelectedPhotoUri = result.data!!.data

                    try {
                        Glide.with(this)
                            .load(mSelectedPhotoUri)
                            .centerCrop()
                            .into(binding.civProfile)
                    } catch (e: Exception) {
                    }
                }
            }
        }


    fun setUserDataInUI(user: FireUser) {
        mUserDetails = user
        Glide
            .with(this)
            .load(user.imageUrl)
            .centerCrop()
            .into(binding.civProfile)

        user.username.let { if (it.isNotEmpty()) binding.etUsernameProfile.setText(it) }
        user.email.let { if (it.isNotEmpty()) binding.etEmailProfile.setText(it) }
        user.mobileNumber.let { if (it.isNotEmpty()) binding.etMobileNumberProfile.setText(it) }
        binding.etEmailProfile.setText(user.email)
        binding.etMobileNumberProfile.setText(user.mobileNumber)
    }

    private fun uploadUserImage() {
        showProgressDialog()
        if (mSelectedPhotoUri != null) {
            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                "USER_IMAGE_" + System.currentTimeMillis() + "." + getFileExtension(
                    this,
                    mSelectedPhotoUri
                )
            )
            sRef.putFile(mSelectedPhotoUri!!).addOnSuccessListener { taskSnapshot ->
                hideProgressDialog()

                taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
                    mProfileImageOnlineDBUri = uri.toString()
                    Log.e(
                        "SEE_URI",
                        mSelectedPhotoUri.toString() + " === " + mProfileImageOnlineDBUri
                    )
                    updateUserProfileData()
                }
            }.addOnFailureListener { e ->
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                hideProgressDialog()
            }
        }
    }

    fun onProfileUpdateSuccess() {
        hideProgressDialog()
        setResult(RESULT_OK)
        finish()
    }

    private fun updateUserProfileData() {
        val userUpdated = HashMap<String, Any>()
        var anyChangesMade = false

        if (mProfileImageOnlineDBUri.isNotEmpty() && mProfileImageOnlineDBUri != mUserDetails.imageUrl){
            userUpdated[Constants.IMAGE_URL] = mProfileImageOnlineDBUri
            anyChangesMade = true
        }

        if (binding.etUsernameProfile.text.toString().isNotEmpty() && binding.etUsernameProfile.text.toString() != mUserDetails.username){
            userUpdated[Constants.USERNAME] = binding.etUsernameProfile.text.toString()
            anyChangesMade = true
        }

        if (binding.etMobileNumberProfile.text.toString().isNotEmpty() && binding.etMobileNumberProfile.text.toString().isDigitsOnly() && (binding.etMobileNumberProfile.text.toString() != mUserDetails.mobileNumber)){
            userUpdated[Constants.MOBILE_NUMBER] = binding.etMobileNumberProfile.text.toString().toLong()
            anyChangesMade = true
        }

        if (binding.etMobileNumberProfile.text.toString().isEmpty() && (binding.etMobileNumberProfile.text.toString() != mUserDetails.mobileNumber)) {
            userUpdated[Constants.MOBILE_NUMBER] = 0
            anyChangesMade = true
        }

        if (!binding.etMobileNumberProfile.text.toString().isDigitsOnly()) {
            Toast.makeText(this, "Mobile must only contain numbers", Toast.LENGTH_SHORT).show()
        }

        if (anyChangesMade) {
            showProgressDialog()
            FirebaseClient.updateUserProfileData(this, userUpdated)
        }
    }
}