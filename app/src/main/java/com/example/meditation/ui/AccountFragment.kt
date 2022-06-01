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
import com.example.meditation.databinding.FragmentAccountBinding
import com.example.meditation.viewmodel.UserViewModel

class AccountFragment : Fragment(), LifecycleOwner {

    private lateinit var binding: FragmentAccountBinding
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        userViewModel.getDataUser()

        binding = FragmentAccountBinding.inflate(layoutInflater, container, false)
        val view = binding. root

        userViewModel.userMutableLiveData.observe(viewLifecycleOwner, Observer {
            if (it != null){
                binding.tvName.text = it.name
                binding.tvEmail.text = it.email
                binding.tvSocialNetwork.text = it.social_network

//                userViewModel.userMutableLiveData.postValue(null)
            }
        })

        binding.imgBack.setOnClickListener { findNavController().popBackStack() }

        binding.lyName.setOnClickListener { findNavController().navigate(R.id.action_accountFragment_to_editNameFragment) }

        return view
    }

}