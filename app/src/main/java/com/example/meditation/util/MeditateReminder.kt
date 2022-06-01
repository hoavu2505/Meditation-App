package com.example.meditation.util

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.example.meditation.R
import com.example.meditation.repository.MeditateReminderRepository
import com.example.meditation.viewmodel.MeditateReminderViewModel

const val notificationID = 1
const val channelID = "channel1"
const val titleExtra = "titleExtra"
const val messageExtra = "messageExtra"

class MeditateReminder: BroadcastReceiver() {

    companion object {
        private var stateAlarm = MutableLiveData<Boolean>(false)

        fun setState(b : Boolean){
            stateAlarm.postValue(b)
        }

        fun getState(): MutableLiveData<Boolean>{
            return stateAlarm
        }
    }



    override fun onReceive(context: Context, intent: Intent) {
        val notification = NotificationCompat.Builder(context, channelID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(intent.getStringExtra(titleExtra))
            .setContentText(intent.getStringExtra(messageExtra))
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationID, notification)
        setState(true)
        val meditateReminderRepository = MeditateReminderRepository()
        meditateReminderRepository.changeState(false)
    }
}