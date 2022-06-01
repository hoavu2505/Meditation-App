package com.example.meditation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.meditation.repository.UserRepository

class UserViewModel : ViewModel() {
    private var userRepository: UserRepository = UserRepository()
    var userMutableLiveData = userRepository.userMutableLiveData

    fun getDataUser(){
        userRepository.getDataUser()
    }

    fun editUsername(name : String){
        userRepository.editUsername(name)
    }
}