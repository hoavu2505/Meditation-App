package com.example.meditation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.meditation.model.Statistic
import com.example.meditation.repository.StatisticRepository

class StatisticViewModel: ViewModel() {
    private var statisticRepository = StatisticRepository()
    var statisticLiveData = statisticRepository.statisticLiveData

    fun getData(){
        statisticRepository.getData()
    }

    fun createData(statistic: Statistic){
        statisticRepository.createData(statistic)
    }

    fun updateData(statistic: Statistic){
        statisticRepository.updateData(statistic)
    }

    fun resetTodayTime(){
        statisticRepository.resetTodayTime()
    }
}