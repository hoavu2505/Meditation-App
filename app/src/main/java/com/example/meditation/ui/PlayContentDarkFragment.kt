package com.example.meditation.ui

import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.util.Log
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
import com.google.android.material.transition.MaterialFadeThrough
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayContentDarkFragment : Fragment() {

    private var mediaPlayer: MediaPlayer? = null
    private lateinit var binding: FragmentPlayContentDarkBinding
    private val args : PlayContentDarkFragmentArgs by navArgs()

    private var isReplay : Boolean = false

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
        val content = args.content

        binding = FragmentPlayContentDarkBinding.inflate(layoutInflater, container, false)
        val view = binding.root

        Theme.changeColorStatusBar(requireActivity().window, R.color.dark_background, context)
        Theme.setStatusBarLightText(requireActivity().window ,true)

        val audio = content.audio!![0]

        ui(content)
        initPlay(audio)

        binding.lyAction.setOnClickListener { handleAction() }

        binding.imgClose.setOnClickListener { findNavController().popBackStack() }

        mediaPlayer?.let {
            it.setOnCompletionListener {
                GlobalScope.launch(Dispatchers.Main) {
                    delay(2000)
//                    handleClose()
                    if (!isReplay){
                        findNavController().popBackStack()
                    }else{
                        it.start()
                    }

                }
            }
        }

        binding.imgReplay.setOnClickListener {
            isReplay = if (!isReplay) {
                binding.imgReplay.setImageResource(R.drawable.ic_replay_enable)
                true
            }else{
                binding.imgReplay.setImageResource(R.drawable.ic_replay_disable)
                false
            }
        }

        return view
    }

    private fun handleClose() {
        mediaPlayer?.let {
            it.stop()
            it.release()
        }

        findNavController().popBackStack()
    }

    private fun handleAction() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                binding.imgAction.setImageResource(R.drawable.ic_play)
            } else if (!it.isPlaying){
                it.start()
                binding.imgAction.setImageResource(R.drawable.ic_pause)
            }
        }
    }

    private fun initPlay(audio: String) {
        mediaPlayer = MediaPlayer()
        mediaPlayer?.let { player ->
            player.setDataSource(audio)
            player.prepareAsync()
            player.setOnPreparedListener {
                it.start()
                binding.imgAction.setImageResource(R.drawable.ic_pause)
                binding.lyAction.visibility = View.VISIBLE
                binding.lyProgress.visibility = View.INVISIBLE

                initProgressbar(it)
            }
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

    private fun ui(content: Content) {
        binding.tvMode.text = content.mode
        binding.tvContentName.text = content.name
    }

    override fun onDestroy() {
        super.onDestroy()

        mediaPlayer?.let {
            try {
                it.stop()
                it.release()
                mediaPlayer = null
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

}