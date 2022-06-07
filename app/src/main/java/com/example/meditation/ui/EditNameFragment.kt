package com.example.meditation.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.meditation.databinding.FragmentEditNameBinding
import com.example.meditation.viewmodel.UserViewModel
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough

class EditNameFragment : Fragment(), LifecycleOwner {

    private lateinit var binding : FragmentEditNameBinding
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialFadeThrough().apply {
            duration = 300L
        }

        returnTransition = MaterialElevationScale(true).apply {
            duration = 50L
        }

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        userViewModel.getDataUser()

        binding = FragmentEditNameBinding.inflate(layoutInflater, container, false)
        val view = binding.root

        userViewModel.userMutableLiveData.observe(viewLifecycleOwner, Observer {
            if (it != null){
                binding.edtName.setText(it.name, TextView.BufferType.EDITABLE)
            }
        })

        binding.imgBack.setOnClickListener { findNavController().popBackStack() }

        binding.btnSave.setOnClickListener {
            userViewModel.editUsername(binding.edtName.text.toString())
            findNavController().popBackStack()
        }

        return view
    }

}