package com.ktxdevelopment.mobiware.ui.fragments.additional

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.os.postDelayed
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ktxdevelopment.mobiware.R
import com.ktxdevelopment.mobiware.clients.main.BaseClient
import com.ktxdevelopment.mobiware.clients.main.PermissionClient
import com.ktxdevelopment.mobiware.clients.main.TextInputClient
import com.ktxdevelopment.mobiware.clients.firebase.FirebaseClient
import com.ktxdevelopment.mobiware.databinding.FragmentFeedbackBinding
import com.ktxdevelopment.mobiware.models.firebase.FireFeedback
import com.ktxdevelopment.mobiware.ui.activities.BaseActivity
import com.ktxdevelopment.mobiware.ui.fragments.main.BaseFragment
import com.ktxdevelopment.mobiware.ui.recview.LinkedImageAdapter
import com.ktxdevelopment.mobiware.util.Constants
import com.ktxdevelopment.mobiware.util.tryEr
import com.ktxdevelopment.mobiware.viewmodel.LocalViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentFeedback : BaseFragment(),  LinkedImageAdapter.OnLinkedImageClickListener {
     private lateinit var binding: FragmentFeedbackBinding
     private lateinit var photoList: ArrayList<Uri>
     private lateinit var mAdapter: LinkedImageAdapter
     private lateinit var viewModel: LocalViewModel

     override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
          binding = FragmentFeedbackBinding.inflate(inflater, container, false)
          return binding.root
     }



     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
          super.onViewCreated(view, savedInstanceState)
          initializeUI()
          clickListeners()
     }




     fun onFeedbackError() {
          hideProgressDialog()
          showErrorSnackbar(getString(R.string.smth_went_wrong_check_connection))
     }

     fun onFeedbackSuccess(feedback: FireFeedback) {
          hideProgressDialog()
          showSuccessSnackbar(getString(R.string.feedback_successfully))
          tryEr {
               if (photoList.isNotEmpty()) viewModel.writeFeedbackPhotosToFirestore(
                    context!!,
                    photoList,
                    feedback
               )
               Handler(Looper.getMainLooper()).postDelayed(600) { activity!!.finish() }
          }
     }


     @Deprecated("Deprecated in Java")
     override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
          if (requestCode == Constants.READ_STORAGE_CODE) {
               if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) showImageChooser()
               else showErrorSnackbar(getString(R.string.denied_permission))
          }
     }


     private fun showImageChooser() {
          val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
          galleryIntent.type = "image/*"
          galleryResultLauncher.launch(galleryIntent)
     }


     private val galleryResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
          if (result.resultCode == AppCompatActivity.RESULT_OK && result.data != null) {
               tryEr {
                    result.data!!.data?.let {
                         if (!BaseClient.checkIfUriExistsInList(activity!!.contentResolver, it, photoList)) {
                              photoList.add(it)
                              mAdapter.submitList(photoList)
                              mAdapter.notifyItemInserted(photoList.size - 1)
                              if (photoList.size == 3) {
                                   binding.cvInsertImage.visibility = View.GONE
                              }
                         }
                    }
               }
          }
     }

     override fun onLinkedClick(position: Int) {
          try {
               photoList.removeAt(position)
               mAdapter.submitList(photoList)
               mAdapter.notifyDataSetChanged()
               mAdapter.notifyItemRemoved(position)
               binding.cvInsertImage.apply {
                    if (visibility == View.GONE) { visibility = View.VISIBLE
                    }
               }
          }catch (e: Exception) { }
     }

     private fun initializeUI() {
          setActionBarTitle(getString(R.string.send_feedback))

          viewModel = ViewModelProvider(requireActivity())[LocalViewModel::class.java]

          mAdapter = LinkedImageAdapter(this)
          binding.rvLinkedImages.apply {
               layoutManager = LinearLayoutManager(context)
               adapter = mAdapter
          }
          photoList = ArrayList()
     }

     private fun clickListeners() {
          val userEmail: String = activity?.intent?.getStringExtra(Constants.USER_EXTRA) ?: getString(R.string.null_email_placeholder_for_feedback)

          binding.btnSendFeedback.setOnClickListener {
               if (TextInputClient.validateFilledInput(binding.etFeedback.text.toString()) || photoList.size > 0) {
                    showProgressDialogCancellable()
                    FirebaseClient.sendFeedback(this, userEmail, binding.etFeedback.text.toString())
               } else binding.etFeedback.error = getString(R.string.no_empty_input)
          }

          binding.cvInsertImage.setOnClickListener {
               tryEr {
                    if (PermissionClient.hasGalleryPermissions(context!!)) showImageChooser()
                    else ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), Constants.READ_STORAGE_CODE)
               }
          }
     }
}