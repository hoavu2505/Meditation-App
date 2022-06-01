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
import com.example.meditation.databinding.FragmentPlayContentDarkBinding
import com.example.meditation.model.Content
import com.example.meditation.theme.Theme

class PlayContentDarkFragment : Fragment() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var binding: FragmentPlayContentDarkBinding
    private val args : PlayContentDarkFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val content = args.content

        binding = FragmentPlayContentDarkBinding.inflate(layoutInflater, container, false)
        val view = binding.root

        Theme.changeColorStatusBar(requireActivity().window, R.color.dark_background, context)
        Theme.setStatusBarLightText(requireActivity().window ,true)

        val audio = content.audio!![0]

        ui(content)
        initPlay(audio)

        binding.lyAction.setOnClickListener { handleAction() }

        binding.imgClose.setOnClickListener { handleClose() }

        mediaPlayer.setOnCompletionListener {
            Handler().postDelayed({
                handleClose()
            }, 2000)
        }

        return view
    }

    private fun handleClose() {
        findNavController().popBackStack()
        mediaPlayer.stop()
        mediaPlayer.release()
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
        }
    }

    private fun ui(content: Content) {
        binding.tvMode.text = content.mode
        binding.tvContentName.text = content.name
    }


}