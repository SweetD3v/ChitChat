package com.demo.chitchat.ui.activities

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.demo.chitchat.utils.isDarkModeEnabled
import com.demo.chitchat.utils.isOreoPlus
import com.demo.chitchat.utils.isPiePlus
import com.demo.chitchat.utils.isRPlus

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {
    abstract val binding: VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setFullScreen()
        setSystemBarsColors()
    }
}

fun Activity.setFullScreen() {
    window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)

    //For immersive screen only
//    window.decorView.systemUiVisibility = (
//            View.SYSTEM_UI_FLAG_IMMERSIVE
//                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
}

var inSplash = false
var inLogin = false

fun Activity.setSystemBarsColors() {
    if (isOreoPlus()) {
        val flags = window.decorView.systemUiVisibility
        if (inSplash || inLogin) {
            window.clearFlags(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
            window.clearFlags(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR)
        } else if (isDarkModeEnabled()) {
            window.clearFlags(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
            window.clearFlags(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR)
        } else {
            window.decorView.systemUiVisibility = (flags
                    or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                    or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        }
    }
}

@Suppress("DEPRECATION")
fun Activity.showSystemUI() {
    if (isPiePlus()) {
        window.attributes.layoutInDisplayCutoutMode =
            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
    }
    if (isRPlus()) {
        // show app content in fullscreen, i. e. behind the bars when they are shown (alternative to
        // deprecated View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION and View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        window.setDecorFitsSystemWindows(false)
        // finally, show the system bars
        window.insetsController?.show(WindowInsets.Type.systemBars())
    } else {
        // Shows the system bars by removing all the flags
        // except for the ones that make the content appear under the system bars.
//        val flags = window.decorView.systemUiVisibility
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }
}