package com.example.meditation.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.meditation.model.Content
import com.example.meditation.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges

class UserRepository {
    var userMutableLiveData = MutableLiveData<User?>()
    private val db : FirebaseFirestore = FirebaseFirestore.getInstance()
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    init {
        if (mAuth.currentUser!= null){
            val docRef = db.collection("User")
                .document(mAuth.currentUser!!.uid)

            docRef.addSnapshotListener { value, error ->
                if (error != null){
                    return@addSnapshotListener
                }

                if (value != null && value.exists()){
                    val user: User = User(
                        value.data!!["id"] as String,
                        value.data!!["name"] as String,
                        value.data!!["email"] as String,
                        value.data!!["avatar"] as String,
                        value.data!!["social_network"] as String
                    )
                    userMutableLiveData.postValue(user)
                }
            }

        }else{
            userMutableLiveData.postValue(null)
        }
    }

    fun getDataUser() {
        mAuth.currentUser?.let {
            val docRef = db.collection("User")
                .document(mAuth.currentUser!!.uid)

            docRef.addSnapshotListener { value, error ->
                if (error != null){
                    return@addSnapshotListener
                }

                if (value != null && value.exists()){
                    val user: User = User(
                        value.data!!["id"] as String,
                        value.data!!["name"] as String,
                        value.data!!["email"] as String,
                        value.data!!["avatar"] as String,
                        value.data!!["social_network"] as String
                    )
                    userMutableLiveData.postValue(user)
                }
            }
        }
    }

    fun editUsername(name : String){
        val docRef = db.collection("User")
            .document(mAuth.currentUser!!.uid)
        docRef.update("name", name)
    }
}