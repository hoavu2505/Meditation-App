package com.example.meditation

import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.content.res.AppCompatResources
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.meditation.databinding.ActivityMainBinding
import com.example.meditation.theme.NavBar


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
            if(destination.id == R.id.sleepFragment) NavBar.changeDarkBackgroundColor(this, binding.bottomNavigation)

            else NavBar.changeLightBackgroundColor(this, binding.bottomNavigation)
        }

    }

}


