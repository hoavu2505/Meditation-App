package com.example.meditation.ui

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.meditation.R
import com.example.meditation.theme.NavBar
import com.example.meditation.theme.Theme
import com.example.meditation.viewmodel.FirebaseAuthViewModel
import com.google.android.material.transition.MaterialFadeThrough

class SplashFragment : Fragment(), LifecycleOwner {

    private lateinit var firebaseAuthViewModel: FirebaseAuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuthViewModel = ViewModelProvider(this)[FirebaseAuthViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        hideNavBar()

        val mainActivity = requireActivity().findViewById<RelativeLayout>(R.id.main_activity)
        mainActivity.background = requireActivity().getDrawable(R.color.white)

        Theme.changeColorStatusBar(requireActivity().window, R.color.white, context)
        Theme.setStatusBarLightText(requireActivity().window ,false)

        // Inflate the layout for this fragment
        Handler().postDelayed({
            if (onBoardingFinished()){
                firebaseAuthViewModel.signOutMutableLiveData.observe(viewLifecycleOwner, Observer {
                    if (it) {
                        materialMotion()
                        findNavController().navigate(R.id.action_splashScreenFragment_to_homeLoginFragment)
//                        Log.d("signOut: ", "$it")
                    }
                    else {
                        materialMotion()
                        findNavController().navigate(R.id.action_splashScreenFragment_to_homeFragment)
//                        Log.d("signOut: ", "$it")
                    }
                })

            } else{
                findNavController().navigate(R.id.action_splashScreenFragment_to_onBoardFragment)
            }
        }, 3000)

        return inflater.inflate(R.layout.fragment_splash_screen, container, false)
    }

    private fun onBoardingFinished() : Boolean{
        val sharePref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        return sharePref.getBoolean("Finished", false)
    }

    private fun hideNavBar() {
        NavBar.hideNavBar(requireActivity().findViewById(R.id.bottom_navigation))
    }

    private fun showNavBar() {
        NavBar.showNavBar(requireActivity().findViewById(R.id.bottom_navigation))
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