package com.example.meditation.ui

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import android.widget.Toast
import androidx.core.content.getSystemService
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.meditation.R
import com.example.meditation.databinding.FragmentMeditateReminderBinding
import com.example.meditation.model.Reminder
import com.example.meditation.util.*
import com.example.meditation.viewmodel.MeditateReminderViewModel
import java.util.*

class MeditateReminderFragment : Fragment(), TimePickerDialog.OnTimeSetListener {

    private lateinit var binding: FragmentMeditateReminderBinding
    private lateinit var meditateReminderViewModel : MeditateReminderViewModel
    var hour = 0
    var minute  = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        meditateReminderViewModel = ViewModelProvider(this)[MeditateReminderViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        meditateReminderViewModel.getData()

        meditateReminderViewModel.meditateReminderLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it != null){
                showTimeOnText(it.hour.toInt(), it.minute.toInt())
                hour = it.hour.toInt()
                minute = it.minute.toInt()
                binding.swReminder.isChecked = it.state
            }else{
                showTimeOnText(0,0)
            }
        })

        binding = FragmentMeditateReminderBinding.inflate(layoutInflater, container, false)
        val view = binding.root

        binding.imgBack.setOnClickListener { findNavController().popBackStack() }

        binding.lyTime.setOnClickListener { pickTime() }

        createNotificationChannel()

        binding.swReminder.setOnCheckedChangeListener { compoundButton, ischecked ->
            if (ischecked){
                Toast.makeText(requireContext(),"Checked", Toast.LENGTH_SHORT).show()
                scheduleReminder()
                meditateReminderViewModel.changeState(true)

            }else{
                Toast.makeText(requireContext(),"unChecked", Toast.LENGTH_SHORT).show()
                cancelReminder()
                meditateReminderViewModel.changeState(false)
            }
        }

        MeditateReminder.getState().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it){
                binding.swReminder.isChecked = false
                meditateReminderViewModel.changeState(false)
            }
        })

        return view
    }

    private fun scheduleReminder(){
        val intent = Intent(requireContext(), MeditateReminder::class.java)
        intent.putExtra(titleExtra, getString(R.string.meditate_reminder_title))
        intent.putExtra(messageExtra, getString(R.string.meditate_reminder_des))

        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val time = getTime()

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )

    }

    private fun cancelReminder(){
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), MeditateReminder::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager.cancel(pendingIntent)
    }

    private fun createNotificationChannel(){
        val name = "Notif Channel"
        val desc = "A Description of the Channel"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(channelID, name, importance)
        channel.description = desc

        val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun getTime(): Long {
        val calendar = Calendar.getInstance()
        var now : Long = System.currentTimeMillis()
        calendar.timeInMillis = now
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        if (now > calendar.timeInMillis){
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return calendar.timeInMillis
    }

    private fun pickTime() {
        TimePickerDialog(requireContext(), this, hour, minute, true).show()
    }

    override fun onTimeSet(view: TimePicker?, h: Int, m: Int) {
        hour = h
        minute = m
        val reminder = Reminder(h.toLong(), m.toLong(), false)

//        showTimeOnText(h, m)
        meditateReminderViewModel.createData(reminder)
    }

    @SuppressLint("SetTextI18n")
    fun showTimeOnText(hour: Int, minute: Int){
        if (hour in 0..9 && minute in 0..9){
            binding.tvTime.text = "0$hour:0$minute"
        }
        else if (hour in 0..9){
            binding.tvTime.text = "0$hour:$minute"
        }
        else if (minute in 0..9){
            binding.tvTime.text = "$hour:0$minute"
        }
        else{
            binding.tvTime.text = "$hour:$minute"
        }
    }
}