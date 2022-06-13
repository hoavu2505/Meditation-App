package com.example.meditation.util

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.meditation.repository.StatisticRepository

class ResetTodayMeditateWorker(context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters){

    private val statisticRepository = StatisticRepository()

    override fun doWork(): Result {
        statisticRepository.resetTodayTime()

        Log.d("doWork", "Success")

        return Result.success()
    }
}