package com.example.meditation.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.meditation.repository.FirebaseAuthRepository

class FirebaseAuthViewModel  : ViewModel() {

    private var firebaseAuthRepository : FirebaseAuthRepository = FirebaseAuthRepository()
    var userMutableLiveData = firebaseAuthRepository.userMutableLiveData
    var signOutMutableLiveData = firebaseAuthRepository.signOutMutableLiveData
    var isEmailAlreadyExists = firebaseAuthRepository.isEmailAlreadyExists
    var isEmailSent = firebaseAuthRepository.isEmailSent

    fun register (name: String,
                  email: String,
                  password: String){
        firebaseAuthRepository.register(name, email, password)
    }

    fun login (email: String, password: String){
        firebaseAuthRepository.login(email, password)
    }

    fun signOut(){
        firebaseAuthRepository.signOut()
    }

    fun firebaseAuthWithGoogle(idToken: String){
        firebaseAuthRepository.firebaseAuthWithGoogle(idToken)
    }

    suspend fun sendPasswordResetEmail(email: String){
        firebaseAuthRepository.sendPasswordResetEmail(email)
    }

}