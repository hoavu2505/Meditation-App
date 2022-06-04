package com.example.meditation.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.meditation.R
import com.example.meditation.databinding.FragmentContentDetailDarkBinding
import com.example.meditation.model.Content
import com.example.meditation.theme.NavBar
import com.example.meditation.theme.Theme
import com.google.android.material.transition.MaterialFadeThrough

class ContentDetailDarkFragment : Fragment() {

    private lateinit var binding: FragmentContentDetailDarkBinding
    private val args: ContentDetailDarkFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialFadeThrough().apply {
            duration = 300L
        }
        returnTransition = MaterialFadeThrough().apply {
            duration = 100L
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val content = args.content
        binding = FragmentContentDetailDarkBinding.inflate(layoutInflater, container, false)
        val view = binding.root

        ui(content)
        hideNavBar()

        Theme.changeColorStatusBar(requireActivity().window, R.color.dark_background, context)
        Theme.setStatusBarLightText(requireActivity().window ,true)

        binding.btnPlay.setOnClickListener {
            play(content)
        }

        binding.imgBack.setOnClickListener {
            findNavController().popBackStack()
            showNavBar()
        }

        return view
    }

    private fun play(content: Content) {
        val action = ContentDetailDarkFragmentDirections.actionContentDetailDarkFragmentToPlayContentDarkFragment(content)
        findNavController().navigate(action)
    }

    private fun ui(content: Content) {
        Glide.with(this).load(content.img).into(binding.imgBackground)
        binding.tvContentName.text = content.name

        binding.tvType.text = content.type
        binding.tvDuration.text = content.duration.toString() + " PHÚT"
        binding.tvDescription.text = content.description
    }

    private fun hideNavBar() {
        NavBar.hideNavBar(requireActivity().findViewById(R.id.bottom_navigation))
    }

    private fun showNavBar() {
        NavBar.showNavBar(requireActivity().findViewById(R.id.bottom_navigation))
    }
}