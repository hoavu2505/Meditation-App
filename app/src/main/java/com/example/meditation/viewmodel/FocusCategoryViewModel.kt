package com.example.meditation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.meditation.repository.FocusCategoryRepository

class FocusCategoryViewModel: ViewModel() {
    private var focusCategoryViewModel = FocusCategoryRepository()
    var categoryListLiveData = focusCategoryViewModel.categoryListLiveData

    fun callData(){
        focusCategoryViewModel.callData()
    }
}