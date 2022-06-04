package com.example.meditation.ui

import android.content.Context
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.navigation.fragment.findNavController
import com.example.meditation.R
import com.example.meditation.theme.NavBar
import com.example.meditation.theme.Theme

class SplashFragment : Fragment() {

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
                findNavController().navigate(R.id.action_splashScreenFragment_to_homeFragment)
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
}