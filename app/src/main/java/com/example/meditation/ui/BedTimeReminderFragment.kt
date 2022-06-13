package com.example.meditation.ui

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import androidx.navigation.fragment.findNavController
import com.example.meditation.R
import com.example.meditation.databinding.FragmentBedTimeReminderBinding
import com.example.meditation.model.Reminder
import com.example.meditation.theme.Theme
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough

class BedTimeReminderFragment : Fragment(), TimePickerDialog.OnTimeSetListener {

    private lateinit var binding : FragmentBedTimeReminderBinding
    private var hour = 0
    private var minute  = 0

    private var layout1 = false
    private var layout2 = false

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
        binding = FragmentBedTimeReminderBinding.inflate(layoutInflater, container, false)

        Theme.changeColorStatusBar(requireActivity().window, R.color.dark_background, context)
        Theme.setStatusBarLightText(requireActivity().window ,true)

        binding.imgBack.setOnClickListener { findNavController().popBackStack() }

        binding.lyTime1.setOnClickListener {
            if (!layout1){
                layout1 = true
                pickTime()
            }
        }

        binding.lyTime2.setOnClickListener {
            if (!layout2){
                layout2 = true
                pickTime()
            }
        }

        return binding.root
    }

    private fun pickTime() {
        TimePickerDialog(requireContext(), this, hour, minute, true).show()
    }

    override fun onTimeSet(p0: TimePicker?, h: Int, m: Int) {
        hour = h
        minute = m
        val reminder = Reminder(h.toLong(), m.toLong(), false)

        if(layout1){
            showTimeOnText1(h, m)
            layout1 = false
        }
        if (layout2){
            showTimeOnText2(h, m)
            layout2 = false
        }
    }

    @SuppressLint("SetTextI18n")
    fun showTimeOnText1(hour: Int, minute: Int){
        if (hour in 0..9 && minute in 0..9){
            binding.tvTime1.text = "0$hour:0$minute"
        }
        else if (hour in 0..9){
            binding.tvTime1.text = "0$hour:$minute"
        }
        else if (minute in 0..9){
            binding.tvTime1.text = "$hour:0$minute"
        }
        else{
            binding.tvTime1.text = "$hour:$minute"
        }
    }

    fun showTimeOnText2(hour: Int, minute: Int){
        if (hour in 0..9 && minute in 0..9){
            binding.tvTime2.text = "0$hour:0$minute"
        }
        else if (hour in 0..9){
            binding.tvTime2.text = "0$hour:$minute"
        }
        else if (minute in 0..9){
            binding.tvTime2.text = "$hour:0$minute"
        }
        else{
            binding.tvTime2.text = "$hour:$minute"
        }
    }

}