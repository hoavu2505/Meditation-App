package com.example.meditation.ui

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.provider.AlarmClock
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import androidx.navigation.fragment.findNavController
import com.example.meditation.R
import com.example.meditation.databinding.FragmentAlarmBinding
import com.example.meditation.model.Reminder
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough
import kotlin.math.min

class AlarmFragment : Fragment(), TimePickerDialog.OnTimeSetListener {

    private lateinit var binding : FragmentAlarmBinding
    private var hour : Int? = null
    private var minute : Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialFadeThrough().apply {
            duration = 300L
        }

        returnTransition = MaterialElevationScale(true).apply {
            duration = 50L
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlarmBinding.inflate(layoutInflater, container, false)
        val view = binding.root

        binding.imgBack.setOnClickListener { findNavController().popBackStack() }

        binding.lyTime.setOnClickListener { pickTime() }

        binding.swAlarm.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (isChecked){

                if (hour != null && minute != null){
                    val intent = Intent(AlarmClock.ACTION_SET_ALARM)
                    intent.putExtra(AlarmClock.EXTRA_HOUR, hour)
                    intent.putExtra(AlarmClock.EXTRA_MINUTES, minute)

                    startActivity(intent)
                }

            }
            else{

                if (hour != null && minute != null){
                    val  intent = Intent(AlarmClock.ACTION_DISMISS_ALARM)
                    intent.putExtra(AlarmClock.EXTRA_HOUR, hour)
                    intent.putExtra(AlarmClock.EXTRA_MINUTES, minute)

                    startActivity(intent)
                }

            }
        }

        return view
    }

    private fun pickTime() {
        TimePickerDialog(requireContext(), this, 0, 0, true)
            .show()
    }

    override fun onTimeSet(view: TimePicker?, h: Int, m: Int) {
        hour = h
        minute = m
        val reminder = Reminder(h.toLong(), m.toLong(), false)

        showTimeOnText(h, m)
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