package com.example.meditation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.meditation.repository.FocusContentRepository

class FocusContentViewModel: ViewModel() {
    private var contentRepository = FocusContentRepository()
    var contentListLiveData = contentRepository.contentListLiveData
    var headingContentLiveData = contentRepository.headingContentLiveData

    fun callData(ID: String){
        contentRepository.callData(ID)
    }

    fun headingContentCallData(){
        contentRepository.headingContentCallData()
    }
}