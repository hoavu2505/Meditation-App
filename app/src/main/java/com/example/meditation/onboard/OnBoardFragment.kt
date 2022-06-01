package com.example.meditation.onboard

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.meditation.R
import com.example.meditation.databinding.FragmentOnBoardBinding
import com.example.meditation.onboard.screens.FirstScreen
import com.example.meditation.onboard.screens.SecondScreen
import com.example.meditation.onboard.screens.ThirdScreen
import com.example.meditation.theme.NavBar
import com.example.meditation.theme.Theme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class OnBoardFragment : Fragment() {
    private lateinit var binding : FragmentOnBoardBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = FragmentOnBoardBinding.inflate(layoutInflater, container, false)
        val view = binding.root

        Theme.changeColorStatusBar(requireActivity().window, R.color.pale_brown, context)
        hideNavBar()

        val fragmentList = arrayListOf<Fragment>(FirstScreen(), SecondScreen(), ThirdScreen())

        val adapter = OnBoardAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )

        val viewPager = binding.viewpagerOnboard

        viewPager.adapter = adapter
        binding.circleIndicator.setViewPager(viewPager)


        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == 2){
                    binding.tvSkip.visibility = View.INVISIBLE
                    binding.btnNext.visibility = View.INVISIBLE
                    binding.btnGetStarted.visibility = View.VISIBLE
                } else {
                    binding.tvSkip.visibility = View.VISIBLE
                    binding.btnNext.visibility = View.VISIBLE
                    binding.btnGetStarted.visibility = View.INVISIBLE
                }
            }
        })

        binding.btnNext.setOnClickListener {
            if (viewPager.currentItem < 2){
                viewPager.currentItem++
            }
        }

        binding.tvSkip.setOnClickListener {
            viewPager.currentItem = 2
        }

        binding.btnGetStarted.setOnClickListener {
            findNavController().navigate(R.id.action_onBoardFragment_to_homeFragment)
            onBoardingFinished()
            showNavBar()
        }

        return view
    }

    private fun hideNavBar() {
        NavBar.hideNavBar(requireActivity().findViewById(R.id.bottom_navigation))
    }

    private fun showNavBar() {
        NavBar.showNavBar(requireActivity().findViewById(R.id.bottom_navigation))
    }

    private fun onBoardingFinished(){
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("Finished",true)
        editor.apply()
    }
}