package com.example.meditation.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.navigation.fragment.findNavController
import com.example.meditation.R
import com.example.meditation.databinding.FragmentNotificationBinding
import com.example.meditation.theme.Theme
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough

class NotificationFragment : Fragment() {

    private lateinit var binding: FragmentNotificationBinding

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
    ): View {
        Theme.changeColorStatusBar(requireActivity().window, R.color.white, context)
        Theme.setStatusBarLightText(requireActivity().window ,false)

        binding = FragmentNotificationBinding.inflate(layoutInflater, container, false)
        val view = binding.root

        binding.imgBack.setOnClickListener { findNavController().popBackStack() }

        binding.tvMeditateReminder.setOnClickListener {
            findNavController().navigate(R.id.action_notificationFragment_to_meditateReminderFragment)
            materialMotion()
        }

        binding.tvAlarm.setOnClickListener {
            findNavController().navigate(R.id.action_notificationFragment_to_alarmFragment)
            materialMotion()
        }

        binding.tvSleepReminder.setOnClickListener {
            findNavController().navigate(R.id.action_notificationFragment_to_bedTimeReminderFragment)
            materialMotion()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
    }

    private fun materialMotion(){
        exitTransition = MaterialFadeThrough().apply {
            duration = 100L
        }

        reenterTransition = MaterialFadeThrough().apply {
            duration = 300L
        }
    }

}