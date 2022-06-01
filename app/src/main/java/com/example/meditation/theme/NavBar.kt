package com.example.meditation.theme

import android.content.Context
import android.content.res.ColorStateList
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import com.example.meditation.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class NavBar {
    companion object{
        fun hideNavBar(navBar: BottomNavigationView) {
            navBar.visibility = View.GONE
        }

        fun showNavBar(navBar: BottomNavigationView) {
            navBar.visibility = View.VISIBLE
        }

        fun changeDarkBackgroundColor(context: Context, bottomNavigationView: BottomNavigationView){
            bottomNavigationView.background = AppCompatResources.getDrawable(context, R.drawable.dark_bottom_nav)
            bottomNavigationView.itemRippleColor = context.getColorStateList(R.color.dark_background_bottom_nav)
            bottomNavigationView.itemTextColor = darkColorStateList(context)
            bottomNavigationView.itemIconTintList = darkColorStateList(context)
        }

        fun changeLightBackgroundColor(context: Context, bottomNavigationView: BottomNavigationView){
            bottomNavigationView.background = AppCompatResources.getDrawable(context, R.drawable.light_bottom_nav)
            bottomNavigationView.itemRippleColor = context.getColorStateList(R.color.white)
            bottomNavigationView.itemTextColor = lightColorStateList(context)
            bottomNavigationView.itemIconTintList = lightColorStateList(context)
        }



        //Color for background tint navigation bar
        private fun darkColorStateList(context: Context,
                                       checkedColor:Int = context.getColor(R.color.main_background),
                                       uncheckedColor:Int = context.getColor(R.color.color_for_dark_background)
        ): ColorStateList {
            val states = arrayOf(
                intArrayOf(android.R.attr.state_checked),
                intArrayOf(-android.R.attr.state_checked)
            )
            val colors = intArrayOf(
                checkedColor, // checked
                uncheckedColor // unchecked
            )
            return ColorStateList(states, colors)
        }

        private fun lightColorStateList(context: Context,
                                        checkedColor:Int = context.getColor(R.color.main_background),
                                        uncheckedColor:Int = context.getColor(R.color.gray_3)
        ): ColorStateList {
            val states = arrayOf(
                intArrayOf(android.R.attr.state_checked),
                intArrayOf(-android.R.attr.state_checked)
            )
            val colors = intArrayOf(
                checkedColor, // checked
                uncheckedColor // unchecked
            )
            return ColorStateList(states, colors)
        }
    }
}