package com.example.meditation.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.meditation.R
import com.example.meditation.databinding.FragmentHomeLoginBinding

class HomeLoginFragment : Fragment() {

    private lateinit var binding : FragmentHomeLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeLoginBinding.inflate(layoutInflater, container, false)
        val view = binding.root

        binding.btnSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_homeLoginFragment_to_signUpFragment)
        }

        binding.textViewLogin.setOnClickListener {
            findNavController().navigate(R.id.action_homeLoginFragment_to_signInFragment)
        }
        // Inflate the layout for this fragment
        return view
    }

}