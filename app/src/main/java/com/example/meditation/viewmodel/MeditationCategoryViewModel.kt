package com.example.meditation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MeditationCategoryViewModel : ViewModel() {
    private var meditationRecyclerViewRepository = com.example.meditation.repository.MeditationCategoryRepository()
    var categoryListLiveData = meditationRecyclerViewRepository.categoryListLiveData

    fun callData() = viewModelScope.launch(Dispatchers.IO){
        meditationRecyclerViewRepository.callData()
    }
}