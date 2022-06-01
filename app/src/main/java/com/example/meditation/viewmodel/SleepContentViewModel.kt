package com.example.meditation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.meditation.repository.SleepContentRepository

class SleepContentViewModel : ViewModel(){
    private var contentRepository = SleepContentRepository()
    var contentListLiveData = contentRepository.contentListLiveData
    var headingContentLiveData = contentRepository.headingContentLiveData

    fun callData(){
        contentRepository.callData()
    }

    fun headingContentCallData(){
        contentRepository.headingContentCallData()
    }
}