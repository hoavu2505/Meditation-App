package com.example.meditation.repository

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.meditation.model.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class FirebaseAuthRepository  {

    var userMutableLiveData = MutableLiveData<FirebaseUser>()
    var signOutMutableLiveData = MutableLiveData<Boolean>()
    var isEmailAlreadyExists = MutableLiveData<Boolean>()
    var isEmailSent = MutableLiveData<Boolean>()
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val defaultAvatar = "https://firebasestorage.googleapis.com/v0/b/meditation-app-ec6d8.appspot.com/o/default_avatar.png?alt=media&token=0674b6b3-c549-4c8f-8f4b-48667092d935"

    init {
        if (mAuth.currentUser != null){
            userMutableLiveData.postValue(mAuth.currentUser)
            signOutMutableLiveData.postValue(false)
        }else{
            signOutMutableLiveData.postValue(true)
        }
    }

    fun register (name: String,
                  email: String,
                  password: String){

        mAuth.fetchSignInMethodsForEmail(email)
            .addOnCompleteListener {
                val check = !it.result.signInMethods!!.isEmpty()

                if (!check){
                    mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful){
                                userMutableLiveData.postValue(mAuth.currentUser)
                                val user: FirebaseUser = mAuth.currentUser!!
                                val mUser = User(user.uid, name, email, defaultAvatar, "none")

                                FirebaseFirestore.getInstance().collection("User")
                                    .document(user.uid)
                                    .set(mUser)
                            }else{
                                Log.e("TAG", "createUserWithEmail:failure", task.exception)
                            }
                        }
                }
                else{
//                    Toast.makeText(application.applicationContext, "Email already have", Toast.LENGTH_SHORT).show()
                    Log.e("email", "Email already exists")
                    isEmailAlreadyExists.postValue(true)
                }
        }


    }

    fun login (email: String?, password: String){
        if (email != null){
            mAuth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener {
                    val check = !it.result.signInMethods!!.isEmpty()

                    if (check){
                        mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful){
                                    userMutableLiveData.postValue(mAuth.currentUser)
                                }else{
                                    Log.e("TAG", "loginUserWithEmail:failure", task.exception)
                                }
                            }
                    }
                    else{
                        isEmailAlreadyExists.postValue(false)
                    }
                }
        }
    }

    fun firebaseAuthWithGoogle(idToken: String){
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener{ task ->
                if (task.isSuccessful){
                    Log.d("googleSignIn", "signInWithCredential: success")

                    userMutableLiveData.postValue(mAuth.currentUser)

                    val user = mAuth.currentUser!!
                    val mUser = User(user.uid,
                        user.displayName.toString(),
                        user.email.toString(),
                        defaultAvatar,
                        "google")

                    FirebaseFirestore.getInstance().collection("User")
                        .document(user.uid)
                        .set(mUser)
                }else{
                    Log.d("googleSignIn", "signInWithCredential: failure", task.exception)
                }
        }
    }

    fun signOut(){
        mAuth.signOut()
        signOutMutableLiveData.postValue(true)
    }

    suspend fun sendPasswordResetEmail(email: String){
        withContext(Dispatchers.IO){

            mAuth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener {
                    val check = !it.result.signInMethods!!.isEmpty()

                    if (check){
                        mAuth.sendPasswordResetEmail(email)
                            .addOnCompleteListener {task ->
                                if (task.isSuccessful){
                                    isEmailSent.postValue(true)
                                }
                            }
                    }else{
                        isEmailAlreadyExists.postValue(false)
                    }

                }

        }
    }

}

