package com.example.meditation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.*
import com.example.meditation.databinding.ActivityMainBinding
import com.example.meditation.theme.NavBar
import com.example.meditation.util.ResetTodayMeditateWorker
import java.util.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    lateinit var controller: NavController
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)
        navHostFragment?.let {
            controller = navHostFragment.findNavController()
        }


        binding.bottomNavigation.setupWithNavController(controller)

        controller.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.sleepFragment -> {
                    NavBar.changeDarkBackgroundColor(this, binding.bottomNavigation)
                    binding.root.background = getDrawable(R.color.dark_background)
                }
                R.id.homeFragment -> {
                    NavBar.changeLightBackgroundColor(this, binding.bottomNavigation)
                    binding.root.background = getDrawable(R.color.white)
                }
                R.id.focusFragment -> {
                    NavBar.changeLightBackgroundColor(this, binding.bottomNavigation)
                    binding.root.background = getDrawable(R.color.white)
                }
                R.id.meditationFragment -> {
                    NavBar.changeLightBackgroundColor(this, binding.bottomNavigation)
                    binding.root.background = getDrawable(R.color.white)
                }
            }
        }

//        printHashKey(applicationContext)

        resetTodayMeditate(0,5)

    }

    private fun resetTodayMeditate(hour: Int, minute : Int) {

        val calendar: Calendar = Calendar.getInstance()
        val nowMillis: Long = calendar.getTimeInMillis()

        if (calendar.get(Calendar.HOUR_OF_DAY) > hour ||
            calendar.get(Calendar.HOUR_OF_DAY) === hour && calendar.get(Calendar.MINUTE) + 1 >= minute
        ) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)

        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val diff: Long = calendar.getTimeInMillis() - nowMillis

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val mRequest = PeriodicWorkRequest.Builder(
            ResetTodayMeditateWorker::class.java,
            24,
            TimeUnit.HOURS
        ).setInitialDelay(diff, TimeUnit.MILLISECONDS)
            .setConstraints(constraints).build()

//        WorkManager.getInstance(this)
//            .enqueueUniquePeriodicWork(
//                "resetTodayTime",
//                ExistingPeriodicWorkPolicy.KEEP,
//                mRequest
//            )
        WorkManager.getInstance(this).enqueue(mRequest)
    }

//    private fun printHashKey(context: Context) {
//
//        try {
//            val info = context.packageManager.getPackageInfo(
//                context.packageName,
//                PackageManager.GET_SIGNATURES
//            )
//            for (signature in info.signatures) {
//                val md = MessageDigest.getInstance("SHA")
//                md.update(signature.toByteArray())
//                val hashKey = String(Base64.encode(md.digest(), 0))
//                Log.i("AppLog", "key:$hashKey=")
//            }
//        } catch (e: Exception) {
//            Log.e("AppLog", "error:", e)
//        }
//
//    }

}


