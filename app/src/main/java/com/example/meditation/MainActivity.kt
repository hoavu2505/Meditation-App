package com.example.meditation

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.meditation.databinding.ActivityMainBinding
import com.example.meditation.theme.NavBar
import com.example.meditation.util.CheckingInternet
import java.security.MessageDigest


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


