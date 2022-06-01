package com.example.meditation.theme

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.core.view.ViewCompat

class Theme{

    companion object{

        fun setStatusBarLightText(window: Window, isLight: Boolean) {
            setStatusBarLightTextOldApi(window, isLight)
            setStatusBarLightTextNewApi(window, isLight)
        }

        fun changeColorStatusBar(window: Window, color: Int, context : Context?){
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

            window.statusBarColor = context!!.resources.getColor(color)
            window.navigationBarColor = context!!.resources.getColor(color)
        }

        fun hideSoftKeyBoard(view : View, context: Context){
            val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }


        private fun setStatusBarLightTextOldApi(window: Window, isLight: Boolean) {
            val decorView = window.decorView
            decorView.systemUiVisibility =
                if (isLight) {
                    decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
                } else {
                    decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                }
        }

        private fun setStatusBarLightTextNewApi(window: Window, isLightText: Boolean) {
            ViewCompat.getWindowInsetsController(window.decorView)?.apply {
                // Light text == dark status bar
                isAppearanceLightStatusBars = !isLightText
            }
        }

    }

}