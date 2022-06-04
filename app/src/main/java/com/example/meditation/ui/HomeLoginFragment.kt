package com.example.meditation.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.meditation.R
import com.example.meditation.databinding.FragmentHomeLoginBinding
import com.example.meditation.theme.NavBar
import com.example.meditation.viewmodel.FirebaseAuthViewModel
import com.example.meditation.viewmodel.HomeContentViewModel
import com.example.meditation.viewmodel.UserViewModel

class HomeLoginFragment : Fragment(), LifecycleOwner {

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