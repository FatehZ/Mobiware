package com.ktxdevelopment.mobiware.clients.firebase

import android.text.Editable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.ktxdevelopment.mobiware.R
import com.ktxdevelopment.mobiware.models.firebase.FireFeedback
import com.ktxdevelopment.mobiware.models.firebase.FireMobile
import com.ktxdevelopment.mobiware.models.firebase.FirePasswordModel
import com.ktxdevelopment.mobiware.models.firebase.FireUser
import com.ktxdevelopment.mobiware.models.rest.product.Data
import com.ktxdevelopment.mobiware.ui.activities.*
import com.ktxdevelopment.mobiware.ui.fragments.additional.FragmentFeedback
import com.ktxdevelopment.mobiware.ui.fragments.additional.FragmentProfile
import com.ktxdevelopment.mobiware.ui.fragments.intro.FragmentForgotPassword
import com.ktxdevelopment.mobiware.ui.fragments.intro.FragmentSignIn
import com.ktxdevelopment.mobiware.ui.fragments.intro.FragmentSignUp
import com.ktxdevelopment.mobiware.ui.fragments.main.BaseFragment
import com.ktxdevelopment.mobiware.util.Constants
import com.ktxdevelopment.mobiware.util.tryEr

object FirebaseClient {

     private const val TAG = "FIR_TAG"

     private val firestore by lazy { FirebaseFirestore.getInstance() }
     private val auth = FirebaseAuth.getInstance()

     private fun registerUserToDB(user: FireUser, context: BaseFragment) {
          firestore.collection(Constants.USERS)
               .document(getCurrentUserId())
               .set(user)
               .addOnSuccessListener {
                    if (context is FragmentSignUp) context.onRegisterSuccess(user)
                    else if (context is FragmentSignIn) context.onSignInSuccess(user)
               }

     }

     private fun registerOrModifyUserToDB(newUser: FireUser, context: FragmentSignIn) {
          firestore.collection(Constants.USERS)
               .document(newUser.userId)
               .get()
               .addOnCompleteListener { snap ->
                    if (snap.result.exists()) {
                         if (snap.result.toObject(FireUser::class.java) != null) {
                              val oldUser = snap.result.toObject(FireUser::class.java)!!

                              val mobileList = ArrayList<String>().apply {
                                   oldUser.let {
                                        var sameMobileExists = false
                                        for (i in it.mobileId) {
                                             this.add(i)
                                             if (newUser.mobileId[0] == i) {
                                                  sameMobileExists =
                                                       true
                                             }
                                        }
                                        if (!sameMobileExists) {
                                             this.add(newUser.mobileId[0])
                                        }
                                   }
                              }

                              val updatedUser =
                                   oldUser.apply { this.mobileId = mobileList }

                              firestore.collection(Constants.USERS)
                                   .document(updatedUser.userId)
                                   .set(updatedUser)
                                   .addOnSuccessListener {
                                        context.onSignInSuccess(updatedUser)
                                   }

                         } else registerUserToDB(newUser, context)
                    } else registerUserToDB(newUser, context)
               }
     }


     fun registerUserAuth(
          context: FragmentSignUp,
          name: Editable,
          email: Editable,
          password: Editable,
          phoneId: String
     ) {

          Firebase.auth.createUserWithEmailAndPassword(email.toString(), password.toString())
               .addOnSuccessListener {
                    val user = FireUser(
                         getCurrentUserId(),
                         name.toString(),
                         "",
                         "",
                         "",
                         arrayListOf(phoneId),
                         email.toString()
                    )
                    registerUserToDB(user, context)
                    firestore.collection(Constants.USER_PASSWORDS)
                         .document(getCurrentUserId())
                         .set(
                              FirePasswordModel(
                                   getCurrentUserId(),
                                   email.toString(),
                                   password.toString()
                              )
                         )
               }.addOnFailureListener {
                    ErrorClient.registerError(
                         (context.activity as IntroductionActivity),
                         it
                    )
               }
     }

     fun signInUserAuth(context: FragmentSignIn, email: Editable, password: Editable, phoneId: String) {
          Firebase.auth.signInWithEmailAndPassword(email.toString(), password.toString())
               .addOnSuccessListener {
                    val user = FireUser(getCurrentUserId(), "", "", "", "", arrayListOf(phoneId), email.toString())
                    registerOrModifyUserToDB(user, context)
               }.addOnFailureListener { ErrorClient.signInError((context.activity as IntroductionActivity), it) }
     }


     fun loadUserData(activity: BaseActivity) {
          tryEr {
               firestore.collection(Constants.USERS).document(getCurrentUserId())
                    .addSnapshotListener { snap, ex ->
                         tryEr {
                              if (ex == null) {
                                   if (snap != null && snap.exists()) {
                                        if (activity is MainActivity) activity.onUserDataObtainedFromFirestore(
                                             snap.toObject(FireUser::class.java)!!
                                        )
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
                                        if (fr is FragmentProfile) fr.onProfileLoadOnlineSuccess(snap.toObject(FireUser::class.java)!!)
                                   }
                              }
                         }
                    }
          }
     }

     fun sendMobileDataInBack(mobile: Data) {
          val mobiRef = firestore.collection(Constants.MOBILES).document(mobile.phone_name)
          if (mobiRef.get().result.exists()) {
               mobiRef.get().addOnSuccessListener {
                    mobiRef.set(it.toObject(FireMobile::class.java)!!.apply {
                         userId.add(getCurrentUserId())
                    })
               }
          } else { mobiRef.set(FireMobile(mobile, arrayListOf(getCurrentUserId()))) }
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
          if (context is FragmentForgotPassword) {
               auth.sendPasswordResetEmail(email).addOnSuccessListener {
                    context.onResetPasswordSuccess()
               }.addOnFailureListener {
                    context.hideProgressDialog()
                    if (it is FirebaseAuthInvalidUserException) context.onResetPasswordError(context.getString(R.string.user_does_not_exist))
                    else context.onResetPasswordError(context.getString(R.string.smth_went_wrong_only))
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
}


