package com.example.meditation.ui

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.meditation.R
import com.example.meditation.databinding.FragmentPlayContentLightBinding
import com.example.meditation.model.Content
import com.example.meditation.model.Statistic
import com.example.meditation.viewmodel.StatisticViewModel
import com.google.android.material.transition.MaterialFadeThrough
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayContentLightFragment : Fragment(), LifecycleOwner {

    private var mediaPlayer : MediaPlayer? = null
    private lateinit var binding: FragmentPlayContentLightBinding
    private val args : PlayContentLightFragmentArgs by navArgs()

    var todayTimeMeditate : Long = 0
    var totalTimeMeditate : Long = 0
    var sessionsCompleted : Long = 0

    private lateinit var statisticViewModel: StatisticViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialFadeThrough().apply {
            duration = 300L
        }
        returnTransition = MaterialFadeThrough().apply {
            duration = 100L
        }

        statisticViewModel = ViewModelProvider(this)[StatisticViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val content = args.content
        val audio = args.audio


        binding = FragmentPlayContentLightBinding.inflate(layoutInflater, container, false)
        val view = binding.root

        statisticViewModel.getData()
        statisticViewModel.statisticLiveData.observe(viewLifecycleOwner, Observer { statistic ->
            if (statistic != null){
                statistic.todayTimeMeditate?.let { todayTimeMeditate = it }
                statistic.totalTimeMeditate?.let { totalTimeMeditate = it }
                statistic.sessionsCompleted?.let { sessionsCompleted = it }

            }else{
                statisticViewModel.createData(Statistic(0,0, 0))
            }
        })

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
            findNavController().popBackStack()
        }

        mediaPlayer?.let { it ->
            it.setOnCompletionListener {

                GlobalScope.launch(Dispatchers.Main) {

                    val newTodayMeditate = todayTimeMeditate + it.duration
                    val newTotalTimeMeditate = totalTimeMeditate + it.duration

                    when(content.type){
                        "Course" -> {
                            var newSessions = sessionsCompleted
                            newSessions += 1

                            statisticViewModel.updateData(
                                Statistic(newTodayMeditate, newTotalTimeMeditate, newSessions)
                            )
                        }
                        "Sound" -> statisticViewModel.updateData(
                            Statistic(newTodayMeditate, newTotalTimeMeditate, 0)
                        )
                    }

                    delay(2000)
//                handleClose()
                    findNavController().popBackStack()

                }
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

    private fun ui(content : Content) {
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
//
//        mediaPlayer.stop()
//        mediaPlayer.release()
    }

}