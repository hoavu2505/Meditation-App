package com.example.meditation.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.meditation.model.Content
import com.example.meditation.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore

class UserRepository {
    var userMutableLiveData = MutableLiveData<User?>()
    private val db : FirebaseFirestore = FirebaseFirestore.getInstance()
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun getDataUser() {
        val docRef = db.collection("User")
            .document(mAuth.currentUser!!.uid)
        docRef.get().addOnSuccessListener { document ->
            if (document != null) {
                val user: User = User(
                    document.data!!["id"] as String,
                    document.data!!["name"] as String,
                    document.data!!["email"] as String,
                    document.data!!["avatar"] as String,
                    document.data!!["social_network"] as String
                )
                userMutableLiveData.postValue(user)
            }
        }
    }

    fun editUsername(name : String){
        val docRef = db.collection("User")
            .document(mAuth.currentUser!!.uid)
        docRef.update("name", name)
    }
}