package com.example.meditation.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.meditation.R
import com.example.meditation.databinding.FragmentSettingAccountBinding
import com.example.meditation.viewmodel.FirebaseAuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class SettingAccountFragment : Fragment(), LifecycleOwner {

    private lateinit var binding : FragmentSettingAccountBinding
    private lateinit var firebaseAuthViewModel: FirebaseAuthViewModel

    private lateinit var gsc : GoogleSignInClient
    private lateinit var gso : GoogleSignInOptions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuthViewModel = ViewModelProvider(this)[FirebaseAuthViewModel::class.java]
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        firebaseAuthViewModel.userMutableLiveData.observe(viewLifecycleOwner, Observer {
            if (it != null){
                binding.tvEmailAccount.text = resources.getString(R.string.account) + it.email
            }
        })

        firebaseAuthViewModel.signOutMutableLiveData.observe(viewLifecycleOwner, Observer { signOut ->
            if (signOut){
                findNavController().navigate(R.id.action_settingAccountFragment_to_homeLoginFragment)
            }
        })

        binding = FragmentSettingAccountBinding.inflate(layoutInflater, container, false)
        val view = binding.root

        binding.btnSignOut.setOnClickListener {
            signOut()
            binding.btnSignOut.text = null
            binding.progressSignOut.visibility = View.VISIBLE
        }

        binding.imgBack.setOnClickListener { findNavController().popBackStack() }

        binding.tvAccount.setOnClickListener { findNavController().navigate(R.id.action_settingAccountFragment_to_accountFragment) }

        binding.tvNotification.setOnClickListener { findNavController().navigate(R.id.action_settingAccountFragment_to_notificationFragment) }

//        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestEmail()
//            .build()
//
//        gsc = GoogleSignIn.getClient(requireContext(), gso)

        return view
    }

    private fun signOut() {
        Handler().postDelayed({
            firebaseAuthViewModel.signOut()
        }, 2000)
    }
}