package com.ktxdevelopment.mobiware.clients.firebase

import android.text.Editable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.ktxdevelopment.mobiware.clients.Preferences
import com.ktxdevelopment.mobiware.models.firebase.FireUser
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
                .addOnSuccessListener {  }
    }


    fun registerUserAuth(context: SignUpActivity, email: Editable, password: Editable) {
        Firebase.auth.createUserWithEmailAndPassword(email.toString(), password.toString())
            .addOnSuccessListener { context.onRegisterSuccess() }
    }

    fun loadUserData(activity: BaseActivity) {

        firestore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener {

                val loggedUser: FireUser = it.toObject(FireUser::class.java)!!


                when(activity) {
                    is ProfileActivity -> activity.loadUserProfile(loggedUser)
                    is MainActivity -> activity.loadUserMain(loggedUser)
                    is SignInActivity -> activity.loadUserSignIn(loggedUser)
                }

            }
    }

    fun loadMobileData(activity: BaseActivity) {

        firestore.collection(Constants.MOBILES)
            .get()
            .addOnSuccessListener {

//                val loggedUser: FireUser = it.toObject(FireUser::class.java)!!


//                when(activity) {
//                    is ProfileActivity -> activity.loadProfile(loggedUser)
//                    is MainActivity -> activity.loadProfile(loggedUser)
//                    is SignInActivity -> activity.loadProfile(loggedUser)
//                }

            }

    }






    private fun getCurrentUserId() : String {
        return if (auth.currentUser!= null) Firebase.auth.currentUser!!.uid
        else ""
    }
}








//        fun getCurrentUserId(): String {
//            val currentUser = FirebaseAuth.getInstance().currentUser
//            var currentUserId = ""
//            if (currentUser != null) {
//                currentUserId = currentUser.uid
//            }
//            return currentUserId
//        }

//        fun loadUserData(activity: BaseActivity, readBoardsList: Boolean = false) {
//
//            mFireStore.collection(Constants.USERS)
//                .document(getCurrentUserId())
//                .get()
//                .addOnSuccessListener { document ->
//                    val loggedUser: User = document.toObject(User::class.java)!!
//
//                    when (activity) {
//                        is SignInActivity ->{
//                            activity.signInSuccess(loggedUser)
//                        }
//                        is MainActivity -> {
//                            activity.updateNavigationUserDetails(loggedUser, readBoardsList)
//                        }
//                        is MyProfileActivity ->{
//                            activity.setUserDataInUI(loggedUser)
//                        }
//                    }
//                }
//                .addOnFailureListener {
//                        e->
//                    activity.hideProgressDialog()
//                    e.message?.let { Log.w(Constants.DB_WRITE_TAG, it) }
//                }
//        }
//
//        fun updateUserProfileData(activity: MyProfileActivity, userHashMap: HashMap<String, Any>) {
//            mFireStore.collection(Constants.USERS)
//                .document(getCurrentUserId())
//                .update(userHashMap)
//                .addOnSuccessListener {
//                    Log.i("UPDATE_PROFILE", "updateUserProfileData: updated Successfully")
//                    Toast.makeText(activity, "Profile updated Successfully", Toast.LENGTH_SHORT).show()
//                    activity.onProfileUpdateSuccess()
//                }.addOnFailureListener{ e->
//                    activity.hideProgressDialog()
//                    Log.w("UPDATE_PROFILE", e.message.toString() )
//                    Toast.makeText(activity, "Profile update error", Toast.LENGTH_SHORT).show()
//                }
//        }
//
//        fun createBoard(activity: CreateBoardActivity, boardInfo: Board) {
//            mFireStore.collection(Constants.BOARDS)
//                .document()
//                .set(boardInfo, SetOptions.merge())
//                .addOnSuccessListener {
//                    activity.boardCreateSuccess()
//                    Toast.makeText(activity, "Board created successfully", Toast.LENGTH_SHORT).show()
//                }.addOnFailureListener {
//                    activity.hideProgressDialog()
//                    Log.e("CREATE_BOARD", "createBoard : $it")
//                    Toast.makeText(activity, "Sorry, board creating error", Toast.LENGTH_SHORT).show()
//                }
//        }

//        fun getBoardsList(activity: MainActivity) {
//            mFireStore.collection(Constants.BOARDS)
//                .whereArrayContains(Constants.ASSIGNED_TO, getCurrentUserId())
//                .get()
//                .addOnSuccessListener { document->
//                    val boardsList: ArrayList<Board> = ArrayList()
//
//                    for (i in document.documents){
//                        val board = i.toObject(Board::class.java)!!
//                        board.documentID = i.id
//                        boardsList.add(board)
//                    }
//                    activity.populateBoardsListToUI(boardsList)
//                }
//                .addOnFailureListener { e->
//                    activity.hideProgressDialog()
//                    Log.e("ER_ROR", "getBoardsList: === " + e.printStackTrace() )
//                    Toast.makeText(activity.applicationContext, "Error in uploading boards", Toast.LENGTH_SHORT).show()
//                }
//        }

//        fun getBoardDetails(activity: TaskListActivity, documentId: String) {
//            mFireStore.collection(Constants.BOARDS)
//                .document(documentId)
//                .get()
//                .addOnSuccessListener { document ->
//                    Log.i("BOARD_LIST", document.toString())
//                    activity.boardDetails(document.toObject(Board::class.java)!!)
//                }
//                .addOnFailureListener { e->
//                    activity.hideProgressDialog()
//                    Log.e("ER_ROR", "getTaskList: === " + e.printStackTrace() )
//                    Toast.makeText(activity.applicationContext, "Error in downloading tasks", Toast.LENGTH_SHORT).show()
//                }
//        }
