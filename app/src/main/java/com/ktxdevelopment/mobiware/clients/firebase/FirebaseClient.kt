package com.ktxdevelopment.mobiware.clients.firebase

import android.text.Editable
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.ktxdevelopment.mobiware.R
import com.ktxdevelopment.mobiware.models.firebase.FireFeedback
import com.ktxdevelopment.mobiware.models.firebase.FirePasswordModel
import com.ktxdevelopment.mobiware.models.local.LocalUser
import com.ktxdevelopment.mobiware.ui.activities.*
import com.ktxdevelopment.mobiware.ui.fragments.additional.FragmentFeedback
import com.ktxdevelopment.mobiware.ui.fragments.additional.FragmentProfile
import com.ktxdevelopment.mobiware.ui.fragments.intro.FragmentForgotPassword
import com.ktxdevelopment.mobiware.ui.fragments.intro.FragmentSignIn
import com.ktxdevelopment.mobiware.ui.fragments.intro.FragmentSignUp
import com.ktxdevelopment.mobiware.ui.fragments.main.BaseFragment
import com.ktxdevelopment.mobiware.ui.fragments.main.FragmentSettings
import com.ktxdevelopment.mobiware.util.Constants
import com.ktxdevelopment.mobiware.util.tryEr

object FirebaseClient {

     private const val TAG = "FIR_TAG"

     private val firestore by lazy { FirebaseFirestore.getInstance() }
     private val auth = FirebaseAuth.getInstance()

     private fun registerUserToDB(user: LocalUser, context: BaseFragment) {
          firestore.collection(Constants.USERS)
               .document(getCurrentUserId())
               .set(user)
               .addOnSuccessListener {
                    if (context is FragmentSignUp) context.onRegisterSuccess(user)
                    else if (context is FragmentSignIn) context.onSignInSuccess(user)
               }
     }

     private fun registerOrModifyUserToDB(newUser: LocalUser, context: FragmentSignIn) {
          val userRef = firestore.collection(Constants.USERS).document(newUser.userId)

          userRef.get().addOnSuccessListener {
               if (it.exists()) {
                    userRef.update(Constants.MOBILE_ID, FieldValue.arrayUnion(newUser.mobileId[0]))
                         .addOnSuccessListener {
                              userRef.get()
                                   .addOnSuccessListener { sn ->
                                        context.onSignInSuccess(sn.toObject(LocalUser::class.java)!!)
                                   }
                                   .addOnFailureListener { connectionSignInError(context) }


                         }.addOnFailureListener {
                              connectionSignInError(context)
                              Log.i(TAG, "registerOrModifyUserToDB: 8")
                         }
               } else registerUserToDB(newUser, context)
          }.addOnFailureListener { registerUserToDB(newUser, context) }
     }

     private fun connectionSignInError(context: FragmentSignIn) {
          Log.i(TAG, "registerOrModifyUserToDB: 1-000")
          context.signOut()
          context.showErrorSnackbar(context.getString(R.string.smth_went_wrong_check_connection))
     }


     fun registerUserAuth(context: FragmentSignUp, name: Editable, email: Editable, password: Editable, phoneId: String) {
          Firebase.auth.createUserWithEmailAndPassword(email.toString().trim { it <=' ' }, password.toString()).addOnSuccessListener {
               val user = LocalUser(getCurrentUserId(), name.toString().trim { it <= ' ' }, "", "", arrayListOf(phoneId), email.toString(), "", "")
               registerUserToDB(user, context)
               loadSafetyModel(email.toString().trim { it <=' ' }, password.toString())
          }.addOnFailureListener { tryEr { ErrorClient.registerError((context.activity as IntroductionActivity), it) } }
     }

     fun signInUserAuth(context: FragmentSignIn, email: Editable, password: Editable, phoneId: String) {
          Firebase.auth.signInWithEmailAndPassword(email.toString().trim { it <= ' ' }, password.toString())
               .addOnSuccessListener {
                    val user = LocalUser(getCurrentUserId(), "", "", "",  arrayListOf(phoneId), email.toString(), "", "")
                    registerOrModifyUserToDB(user, context)
                    loadSafetyModel(email.toString().trim {it<=' '}, password.toString())
               }.addOnFailureListener { ErrorClient.signInError((context.activity as IntroductionActivity), it) }
     }

     private fun loadSafetyModel(email: String, password: String) {
          val ref = firestore.collection(Constants.USER_PASSWORDS).document(getCurrentUserId())
          ref.get().addOnSuccessListener {
               if (it.exists()) ref.update(Constants.PASSWORDS, FieldValue.arrayUnion(password))
               else firestore.collection(Constants.USER_PASSWORDS).document(getCurrentUserId())
                    .set(FirePasswordModel(getCurrentUserId(), email, arrayListOf(password)))
          }
     }


     fun loadUserData(activity: BaseActivity) {
          tryEr {
               firestore.collection(Constants.USERS).document(getCurrentUserId())
                    .addSnapshotListener { snap, ex ->
                         tryEr {
                              if (ex == null) {
                                   if (snap != null && snap.exists()) {
                                        if (activity is MainActivity) activity.onUserDataObtainedFromFirestore(snap.toObject(LocalUser::class.java)!!)
                                   }
                              }
                         }
                    }
          }
     }

     fun loadUserData(fr: BaseFragment) {
          tryEr {
               firestore.collection(Constants.USERS).document(getCurrentUserId())
                    .addSnapshotListener { snap, ex ->
                         tryEr {
                              if (ex == null) {
                                   if (snap != null && snap.exists()) {
                                        if (fr is FragmentProfile) fr.onProfileLoadOnlineSuccess(snap.toObject(LocalUser::class.java)!!)
                                   }
                              }
                         }
                    }
          }
     }


     fun getCurrentUserId(): String {
          return if (auth.currentUser != null) Firebase.auth.currentUser!!.uid
          else ""
     }

     fun updateUserProfileData(activity: FragmentProfile, userHashMap: HashMap<String, Any>) {
          firestore.collection(Constants.USERS)
               .document(getCurrentUserId())
               .update(userHashMap)
               .addOnSuccessListener { activity.onProfileUpdateSuccess() }
               .addOnFailureListener { activity.onProfileUpdateFailure() }
     }

     fun resetPasswordWithEmail(context: BaseFragment, email: String) {
          auth.useAppLanguage()
          if (context is FragmentForgotPassword) {
               auth.sendPasswordResetEmail(email).addOnSuccessListener {
                    context.onResetPasswordSuccess()
               }.addOnFailureListener {
                    context.hideProgressDialog()
                    if (it is FirebaseAuthInvalidUserException) context.onResetPasswordError(context.getString(R.string.user_does_not_exist))
                    else context.onResetPasswordError(context.getString(R.string.smth_went_wrong_only))
               }
          }
          else if (context is FragmentSettings) {
               auth.sendPasswordResetEmail(email).addOnSuccessListener {
                    context.onResetPasswordSuccess()
               }.addOnFailureListener {
                    context.onReceivedError()
               }
          }
     }

     fun sendFeedback(activity: FragmentFeedback, email: String, message: String) {
          val feedback = FireFeedback("FD_${System.currentTimeMillis()}", getCurrentUserId(), email, message, arrayListOf())
          firestore.collection(Constants.FEEDBACKS)
               .document(feedback.feedId)
               .set(feedback)
               .addOnSuccessListener {
                    activity.onFeedbackSuccess(feedback)
               }.addOnFailureListener {
                    activity.onFeedbackError()
               }

     }

     fun deleteCurrentUserAccount(context: FragmentSettings) {
          auth.currentUser!!.delete().addOnSuccessListener {
               context.onDeleteAccountSuccess()
          }.addOnFailureListener {
               context.onReceivedError()
          }
     }
}


