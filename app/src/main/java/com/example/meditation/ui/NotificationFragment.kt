package com.example.meditation.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.meditation.R
import com.example.meditation.databinding.FragmentNotificationBinding

class NotificationFragment : Fragment() {

    private lateinit var binding: FragmentNotificationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotificationBinding.inflate(layoutInflater, container, false)
        val view = binding.root

        binding.imgBack.setOnClickListener { findNavController().popBackStack() }

        binding.tvMeditateReminder.setOnClickListener { findNavController().navigate(R.id.action_notificationFragment_to_meditateReminderFragment) }

        binding.tvAlarm.setOnClickListener { findNavController().navigate(R.id.action_notificationFragment_to_alarmFragment) }

        binding.tvSleepReminder.setOnClickListener { findNavController().navigate(R.id.action_notificationFragment_to_bedTimeReminderFragment) }

        return view
    }

}