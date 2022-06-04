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

class BedTimeReminderFragment : Fragment() {

    private lateinit var binding : FragmentBedTimeReminderBinding

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