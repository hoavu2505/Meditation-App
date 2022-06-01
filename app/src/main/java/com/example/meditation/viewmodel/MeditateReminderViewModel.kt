package com.example.meditation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.meditation.model.Reminder
import com.example.meditation.repository.MeditateReminderRepository

class MeditateReminderViewModel : ViewModel() {
    private var meditateReminderRepository = MeditateReminderRepository()
    var meditateReminderLiveData = meditateReminderRepository.meditateReminderLiveData

    fun getData(){
        meditateReminderRepository.getData()
    }

    fun createData(meditateReminder : Reminder){
        meditateReminderRepository.createData(meditateReminder)
    }

    fun changeState(state : Boolean){
        meditateReminderRepository.changeState(state)
    }
}