package com.example.meditation.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.meditation.R
import com.example.meditation.databinding.FragmentHomeLoginBinding
import com.example.meditation.theme.NavBar
import com.example.meditation.util.CheckingInternet
import com.example.meditation.viewmodel.FirebaseAuthViewModel
import com.example.meditation.viewmodel.HomeContentViewModel
import com.example.meditation.viewmodel.UserViewModel
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough

class HomeLoginFragment : Fragment(), LifecycleOwner {

    private lateinit var binding : FragmentHomeLoginBinding
    private val checkingInternet by lazy { CheckingInternet(requireActivity().application) }

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

        binding = FragmentHomeLoginBinding.inflate(layoutInflater, container, false)
        val view = binding.root

        checkingInternet.isConnected(requireActivity().application)

        checkingInternet.observe(viewLifecycleOwner, Observer {
            if (it){
                binding.btnSignUp.setOnClickListener {
                    materialMotion()
                    findNavController().navigate(R.id.action_homeLoginFragment_to_signUpFragment)
                }

                binding.textViewLogin.setOnClickListener {
                    materialMotion()
                    findNavController().navigate(R.id.action_homeLoginFragment_to_signInFragment)
                }
            }else{
                binding.btnSignUp.setOnClickListener {
                    Toast.makeText(requireContext(), "Không có kết nối Internet. Không thể thao tác.", Toast.LENGTH_SHORT).show()
                }

                binding.textViewLogin.setOnClickListener {
                    Toast.makeText(requireContext(), "Không có kết nối Internet. Không thể thao tác.", Toast.LENGTH_SHORT).show()
                }
            }

            Log.d("network: ", "$it")
        })


        // Inflate the layout for this fragment
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