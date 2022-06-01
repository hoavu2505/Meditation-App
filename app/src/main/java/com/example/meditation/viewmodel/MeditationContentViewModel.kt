package com.example.meditation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.meditation.repository.MeditationContentRepository

class MeditationContentViewModel: ViewModel() {
    private var contentRepository = MeditationContentRepository()
    var contentListLiveData = contentRepository.contentListLiveData
    var headingContentLiveData = contentRepository.headingContentLiveData

    fun callData(ID: String){
        contentRepository.callData(ID)
    }

    fun headingContentCallData(){
        contentRepository.headingContentCallData()
    }
}