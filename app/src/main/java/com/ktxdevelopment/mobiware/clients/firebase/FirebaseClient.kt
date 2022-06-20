package com.ktxdevelopment.mobiware.clients.firebase

import android.text.Editable
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.ktxdevelopment.mobiware.R
import com.ktxdevelopment.mobiware.models.firebase.FireMobile
import com.ktxdevelopment.mobiware.models.firebase.FireUser
import com.ktxdevelopment.mobiware.models.rest.product.Data
import com.ktxdevelopment.mobiware.ui.activities.*
import com.ktxdevelopment.mobiware.util.Constants

object FirebaseClient {

    private const val TAG = "FIR_TAG"

    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private val auth = FirebaseAuth.getInstance()

    private fun registerUserToDB(user: FireUser, context: BaseActivity) {
        firestore.collection(Constants.USERS)
            .document(auth.currentUser!!.uid)
            .set(user)
            .addOnSuccessListener {
                if (context is SignUpActivity) context.onRegisterSuccess(user)
                else if (context is SignInActivity) context.onSignInSuccess(user)
            }
    }

    private fun registerOrModifyUserToDB(newUser: FireUser, context: SignInActivity) {

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
                                        sameMobileExists = true
                                    }
                                }
                                if (!sameMobileExists) {
                                    this.add(newUser.mobileId[0])
                                }
                            }
                        }

                        val updatedUser = oldUser.apply { this.mobileId = mobileList }

                        firestore.collection(Constants.USERS)
                            .document(updatedUser.userId)
                            .set(updatedUser)
                            .addOnSuccessListener { context.onSignInSuccess(updatedUser) }

                    } else registerUserToDB(newUser, context)
                }else registerUserToDB(newUser, context)
            }

    }


    fun registerUserAuth(context: SignUpActivity, name: Editable, email: Editable, password: Editable, phoneId: String) {
        Firebase.auth.createUserWithEmailAndPassword(email.toString(), password.toString())
            .addOnSuccessListener {
                val user = FireUser(getCurrentUserId(), name.toString(), "", "", arrayListOf(phoneId), email.toString())
                registerUserToDB(user, context)
            }.addOnFailureListener { ErrorClient.registerError(context, it) }
    }

    fun signInUserAuth(context: SignInActivity, email: Editable, password: Editable, phoneId: String) {
        Firebase.auth.signInWithEmailAndPassword(email.toString(), password.toString())
            .addOnSuccessListener {
                val user = FireUser(getCurrentUserId(), "","", "", arrayListOf(phoneId), email.toString())
                registerOrModifyUserToDB(user, context)
            }.addOnFailureListener { ErrorClient.signInError(context, it) }
    }





    fun loadUserData(activity: BaseActivity) {

        firestore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener {
                val loggedUser: FireUser = it.toObject(FireUser::class.java)!!

                when (activity) {
                    is ProfileActivity -> activity.setUserDataInUI(loggedUser)
                }
            }
    }

    fun loadMobileDataInBack(mobile: Data) {

        val mobiRef = firestore.collection(Constants.MOBILES).document(mobile.phone_name)

        if (mobiRef.get().result.exists()) {
            mobiRef.get().addOnSuccessListener {
                mobiRef.set(it.toObject(FireMobile::class.java)!!.apply {
                    userId.add(getCurrentUserId())
                })
            }
        }else {
            mobiRef.set(FireMobile(mobile, arrayListOf(getCurrentUserId())))
        }
    }


    fun getCurrentUserId(): String {
        return if (auth.currentUser != null) Firebase.auth.currentUser!!.uid
        else ""
    }

    fun signOut() {
        auth.signOut()
    }


    fun updateUserProfileData(activity: ProfileActivity, userHashMap: HashMap<String, Any>) {
        firestore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener {
                Log.i(TAG, "updateUserProfileData: updated Successfully")
                Toast.makeText(activity, activity.getString(R.string.profile_updated_success), Toast.LENGTH_SHORT).show()
                activity.onProfileUpdateSuccess()
            }.addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.i(TAG, e.message.toString())
                Toast.makeText(activity, activity.getString(R.string.smth_went_wrong), Toast.LENGTH_SHORT).show()
            }
    }

    fun resetPasswordWithEmail(context: BaseActivity, email: String) {
        if (context is ForgotPasswordActivity) {
            auth.sendPasswordResetEmail(email).addOnSuccessListener {
                context.onResetPasswordSuccess()
                Log.i(TAG, "reset: Success")

            }.addOnFailureListener {
                Log.i(TAG, "reset: ${it.message}")
//                context.onResetPasswordError()
            }
        }
    }
}
