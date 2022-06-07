package com.example.meditation.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.meditation.R
import com.example.meditation.databinding.FragmentBedTimeReminderBinding
import com.example.meditation.theme.Theme
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough

class BedTimeReminderFragment : Fragment() {

    private lateinit var binding : FragmentBedTimeReminderBinding

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

        return binding.root
    }

}