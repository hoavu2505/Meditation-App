package com.example.meditation.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.meditation.R
import com.example.meditation.databinding.FragmentContentDetailLightBinding
import com.example.meditation.model.Content
import com.example.meditation.util.CheckingInternet
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.platform.MaterialSharedAxis

class ContentDetailLightFragment : Fragment() {

    private lateinit var binding : FragmentContentDetailLightBinding
    private val args : ContentDetailLightFragmentArgs by navArgs()

    private val checkingInternet by lazy { CheckingInternet(requireActivity().application) }

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
    ): View? {
        val id = args.id
        val content = args.content
        binding = FragmentContentDetailLightBinding.inflate(layoutInflater, container, false)
        val view = binding.root

        ui(content)

        checkingInternet.isConnected(requireActivity().application)

        checkingInternet.observe(viewLifecycleOwner, Observer {
            if (it) binding.btnPlay.setOnClickListener { play(content) }
            else {
                binding.btnPlay.setOnClickListener {
                    Toast.makeText(requireContext(), "Kiểm tra kết nối và thử lại", Toast.LENGTH_SHORT).show()
                }
            }
        })

        binding.imgBack.setOnClickListener { findNavController().popBackStack() }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
    }

    private fun play(content: Content) {
//        Log.d("Play", "${content.audio}")
        val action = ContentDetailLightFragmentDirections.actionContentDetailLightFragmentToPlayContentLightFragment(content, content.audio!![0], 0)
        findNavController().navigate(action)
        materialMotion()
    }

    private fun ui(content: Content) {
        Glide.with(this).load(content.img).into(binding.imgBackground)
        binding.tvContentName.text = content.name

        when(content.mode){
            "Sound" -> binding.imgType.setImageResource(R.drawable.ic_sound)
            "Video" -> binding.imgType.setImageResource(R.drawable.ic_video)
        }

        binding.tvType.text = content.type
        binding.tvDuration.text = content.duration.toString() + " PHÚT"
        binding.tvDescription.text = content.description
    }

    private fun materialMotion(){
        exitTransition = MaterialFadeThrough().apply {
            duration = 100L
        }

        reenterTransition = MaterialElevationScale(true).apply {
            duration = 300L
        }
    }

}