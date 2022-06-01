package com.example.meditation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.meditation.repository.HomeContentRepository

class HomeContentViewModel : ViewModel() {
    var homeContentRepository = HomeContentRepository()
    var cardContent1 = homeContentRepository.cardContent1
    var cardContent2 = homeContentRepository.cardContent2
    var cardDailyContent = homeContentRepository.cardDailyContent
    var contentListLiveData = homeContentRepository.contentListLiveData

    fun cardContent1CallData(){
        homeContentRepository.cardContent1CallData()
    }

    fun cardContent2CallData(){
        homeContentRepository.cardContent2CallData()
    }

    fun cardDailyContentCallData(){
        homeContentRepository.cardDailyContentCallData()
    }

    fun callData(){
        homeContentRepository.callData()
    }
}