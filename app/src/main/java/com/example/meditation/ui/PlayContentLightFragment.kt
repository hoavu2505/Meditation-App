package com.example.meditation.ui

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.meditation.R
import com.example.meditation.databinding.FragmentPlayContentLightBinding
import com.example.meditation.model.Content
import com.google.android.material.transition.MaterialFadeThrough

class PlayContentLightFragment : Fragment() {

    private lateinit var mediaPlayer : MediaPlayer
    private lateinit var binding: FragmentPlayContentLightBinding
    private val args : PlayContentLightFragmentArgs by navArgs()

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
        val audio = args.audio

        binding = FragmentPlayContentLightBinding.inflate(layoutInflater, container, false)
        val view = binding.root

        when(content.type){
            "Sound" -> {
                binding.lySession.visibility = View.INVISIBLE
            }

            "Course" -> {
                binding.lySession.visibility = View.VISIBLE

                val session = args.position + 1

                binding.tvSession.text = session.toString()
            }
        }

        ui(content)
        initPlay(audio)

        binding.lyAction.setOnClickListener { handleAction() }

        binding.imgClose.setOnClickListener {
            handleClose()
        }

        mediaPlayer.setOnCompletionListener {
            Handler().postDelayed({
                handleClose()
            }, 2000)
        }

        return view
    }

    private fun handleClose() {
        mediaPlayer.stop()
        mediaPlayer.release()
        findNavController().popBackStack()
    }

    private fun handleAction() {
        if (mediaPlayer != null && mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            binding.imgAction.setImageResource(R.drawable.ic_play)
        } else if (mediaPlayer != null && !mediaPlayer.isPlaying){
            mediaPlayer.start()
            binding.imgAction.setImageResource(R.drawable.ic_pause)
        }
    }

    private fun initPlay(audio: String) {
        mediaPlayer = MediaPlayer()
        mediaPlayer.setDataSource(audio)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            mediaPlayer.start()
            binding.imgAction.setImageResource(R.drawable.ic_pause)
            binding.lyAction.visibility = View.VISIBLE
            binding.lyProgress.visibility = View.INVISIBLE

            initProgressbar(mediaPlayer)
        }
    }

    private fun initProgressbar(mediaPlayer: MediaPlayer) {
        binding.progressBar.max = mediaPlayer.duration

        val handler = Handler()
        handler.postDelayed(object : Runnable{
            override fun run() {
                try {
                    binding.progressBar.progress = mediaPlayer.currentPosition
                    handler.postDelayed(this, 0)
                }catch (e: Exception){
                    binding.progressBar.progress = 0
                }
            }
        }, 0)
    }

    private fun ui(content : Content) {
        binding.tvMode.text = content.mode
        binding.tvContentName.text = content.name
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
        mediaPlayer.release()
    }

}