package com.example.meditation.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.meditation.R
import com.example.meditation.databinding.FragmentPlayContentVideoBinding
import com.example.meditation.model.Content
import com.google.android.material.transition.MaterialFadeThrough
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener

class PlayContentVideoFragment : Fragment() {

    private lateinit var binding : FragmentPlayContentVideoBinding
    private val args : PlayContentVideoFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedElementEnterTransition = MaterialFadeThrough().apply {
            duration = 300L
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val content = args.content
        val videoID = content.audio!![0]

        Log.d("videoID", videoID)

        binding = FragmentPlayContentVideoBinding.inflate(layoutInflater, container, false)
        val view = binding.root

        val youTubePlayerView = binding.youtubePlayer

        lifecycle.addObserver(youTubePlayerView)

        ui(content)

        binding.youtubePlayer.addYouTubePlayerListener(object :AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer) {
                super.onReady(youTubePlayer)
                youTubePlayer.loadVideo(videoID, 0F)
//                val defaultPlayerUiController = DefaultPlayerUiController(binding.youtubePlayer, youTubePlayer)
//                binding.youtubePlayer.setCustomPlayerUi(defaultPlayerUiController.rootView)
            }

        })

        binding.imgClose.setOnClickListener { findNavController().popBackStack() }

        return view
    }

    private fun ui(content: Content) {
        binding.tvContentName.text = content.name

        when(content.mode){
            "Video" -> binding.imgType.setImageResource(R.drawable.ic_video)
        }

        binding.tvType.text = content.type
        binding.tvDuration.text = content.duration.toString() + " PHÚT"
        binding.tvDescription.text = content.description
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.youtubePlayer.release()
    }

}