package com.ktxdevelopment.mobiware.clients.firebase

import android.text.Editable
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.ktxdevelopment.mobiware.models.firebase.FireMobile
import com.ktxdevelopment.mobiware.models.firebase.FireUser
import com.ktxdevelopment.mobiware.models.rest.product.Data
import com.ktxdevelopment.mobiware.ui.activities.*
import com.ktxdevelopment.mobiware.util.Constants

object FirebaseClient {

    const val TAG = "FIR_TAG"

    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private val auth = FirebaseAuth.getInstance()

    fun registerUserToDB(user: FireUser) {
        firestore.collection(Constants.USERS)
            .document(auth.currentUser!!.uid)
            .set(user)
            .addOnSuccessListener { }
    }


    fun registerUserAuth(context: SignUpActivity, email: Editable, password: Editable) {
        Firebase.auth.createUserWithEmailAndPassword(email.toString(), password.toString())
            .addOnSuccessListener { context.onRegisterSuccess() }
    }

    fun signInUserAuth(context: SignInActivity, email: Editable, password: Editable) {
        Firebase.auth.signInWithEmailAndPassword(email.toString(), password.toString())
            .addOnSuccessListener { context.onSignInSuccess() }
    }





    fun loadUserData(activity: BaseActivity) {

        firestore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener {
                val loggedUser: FireUser = it.toObject(FireUser::class.java)!!

                when (activity) {
                    is ProfileActivity -> activity.setUserDataInUI(loggedUser)
                    is MainActivity -> activity.loadUserMain(loggedUser)
//                    is SignInActivity -> activity.loadUserSignIn(loggedUser)
                }

            }
    }

    fun loadMobileDataPrivate(mobile: Data) {

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
                Toast.makeText(activity, "Profile updated Successfully", Toast.LENGTH_SHORT).show()
                activity.onProfileUpdateSuccess()
            }.addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.i(TAG, e.message.toString())
                Toast.makeText(activity, "Profile update error", Toast.LENGTH_SHORT).show()

            }
    }
}
