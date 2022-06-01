package com.example.meditation

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.meditation.adapter.AudioCourseAdapter
import com.example.meditation.databinding.FragmentCourseDetailBinding
import com.example.meditation.model.Content

class CourseDetailFragment : Fragment(), AudioCourseAdapter.OnItemClickListener {

    private lateinit var binding : FragmentCourseDetailBinding
    private lateinit var audioRecyclerView: RecyclerView
    private lateinit var adapter: AudioCourseAdapter
    private val args : CourseDetailFragmentArgs by navArgs()
    private lateinit var content : Content

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        content = args.content

        binding = FragmentCourseDetailBinding.inflate(layoutInflater, container, false)

        audioRecyclerView = binding.rcvAudio
        audioRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        adapter = AudioCourseAdapter(content.audio!!, this)
        audioRecyclerView.adapter = adapter

        ui(content)

        binding.imgBack.setOnClickListener { findNavController().popBackStack() }

        return binding.root
    }

    override fun onClickItem(audioCourse: String, position: Int) {
        val action = CourseDetailFragmentDirections.actionCourseDetailFragmentToPlayContentLightFragment(content, audioCourse, position)
        findNavController().navigate(action)
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
}