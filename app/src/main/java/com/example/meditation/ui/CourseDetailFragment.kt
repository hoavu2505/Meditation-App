package com.example.meditation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.meditation.R
import com.example.meditation.adapter.AudioCourseAdapter
import com.example.meditation.databinding.FragmentCourseDetailBinding
import com.example.meditation.model.Content
import com.example.meditation.util.CheckingInternet
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough


class CourseDetailFragment : Fragment(), AudioCourseAdapter.OnItemClickListener {

    private lateinit var binding : FragmentCourseDetailBinding
    private lateinit var audioRecyclerView: RecyclerView
    private lateinit var adapter: AudioCourseAdapter
    private val args : CourseDetailFragmentArgs by navArgs()
    private lateinit var content : Content

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
    ): View {
        binding = FragmentCourseDetailBinding.inflate(layoutInflater, container, false)
        val view = binding.root

        content = args.content

        audioRecyclerView = binding.rcvAudio
        audioRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        adapter = AudioCourseAdapter(content.audio!!, this)
        audioRecyclerView.adapter = adapter

        ui(content)

        checkingInternet.isConnected(requireActivity().application)

        checkingInternet.observe(viewLifecycleOwner, Observer {
            if (!it) Toast.makeText(requireContext(), "Kiểm tra kết nối và thử lại", Toast.LENGTH_SHORT).show()
        })

        binding.imgBack.setOnClickListener { findNavController().popBackStack() }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
    }

    override fun onClickItem(audioCourse: String, position: Int) {
        val action = CourseDetailFragmentDirections.actionCourseDetailFragmentToPlayContentLightFragment(content, audioCourse, position)
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