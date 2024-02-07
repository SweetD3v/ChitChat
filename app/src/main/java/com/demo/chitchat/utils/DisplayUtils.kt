package com.demo.chitchat.utils

import DARK_MODE
import DARK_MODE.DARK
import DARK_MODE.FOLLOW_SYSTEM
import DARK_MODE.LIGHT
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

val Context.windowManager: WindowManager get() = getSystemService(Context.WINDOW_SERVICE) as WindowManager

fun View.onGlobalLayout(callback: () -> Unit) {
    viewTreeObserver?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            if (viewTreeObserver != null) {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                callback()
            }
        }
    })
}

val Context.navigationBarOnSide: Boolean get() = usableScreenSize.x < realScreenSize.x && usableScreenSize.x > usableScreenSize.y

val Context.navigationBarOnBottom: Boolean get() = usableScreenSize.y < realScreenSize.y

val Context.navigationBarHeight: Int get() = if (navigationBarOnBottom && navigationBarSize.y != usableScreenSize.y) navigationBarSize.y else 0

val Context.navigationBarWidth: Int get() = if (navigationBarOnSide) navigationBarSize.x else 0

val Context.portrait get() = resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT

fun View.adjustInsetsBothVisible(
    activity: Activity,
    topMargin: (Int) -> Unit,
    bottomMargin: (Int) -> Unit
) {
    ViewCompat.setOnApplyWindowInsetsListener(
        activity.window.decorView
    ) { _, insets ->
        val statusbarHeight = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top
        val navbarHeight = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
//        Log.e("TAG", "adjustInsetsTop: ${statusbarHeight}")
//        Log.e("TAG", "adjustInsetsBottom: ${navbarHeight}")
//        Log.e("TAG", "insetsVisible: ${insets.isVisible(WindowInsetsCompat.Type.systemBars())}")
        topMargin(statusbarHeight)
        bottomMargin(navbarHeight)
        setOnApplyWindowInsetsListener(null)
        return@setOnApplyWindowInsetsListener insets.consumeSystemWindowInsets()
    }
}

val Context.navigationBarSize: Point
    get() = when {
        navigationBarOnSide -> Point(newNavigationBarHeight, usableScreenSize.y)
        navigationBarOnBottom -> Point(usableScreenSize.x, newNavigationBarHeight)
        else -> Point()
    }

val Context.newNavigationBarHeight: Int
    get() {
        var navigationBarHeight = 0
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        if (resourceId > 0) {
            navigationBarHeight = resources.getDimensionPixelSize(resourceId)
        }
        return navigationBarHeight
    }

val Context.statusBarHeight: Int
    get() {
        var statusBarHeight = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            statusBarHeight = resources.getDimensionPixelSize(resourceId)
        }
        return statusBarHeight
    }

val Context.actionBarHeight: Int
    get() {
        val styledAttributes =
            theme.obtainStyledAttributes(intArrayOf(android.R.attr.actionBarSize))
        val actionBarHeight = styledAttributes.getDimension(0, 0f)
        styledAttributes.recycle()
        return actionBarHeight.toInt()
    }

val Context.usableScreenSize: Point
    get() {
        val size = Point()
        windowManager.defaultDisplay.getSize(size)
        return size
    }

val Context.realScreenSize: Point
    get() {
        val size = Point()
        windowManager.defaultDisplay.getRealSize(size)
        return size
    }

fun Resources.getColoredDrawableWithColor(drawableId: Int, color: Int, alpha: Int = 255): Drawable {
    val drawable = getDrawable(drawableId)
    drawable.mutate().applyColorFilter(color)
    drawable.mutate().alpha = alpha
    return drawable
}

val DARK_GREY = 0xFF333333.toInt()

fun Int.getContrastColor(): Int {
    val y = (299 * Color.red(this) + 587 * Color.green(this) + 114 * Color.blue(this)) / 1000
    return if (y >= 149 && this != Color.BLACK) DARK_GREY else Color.BLACK
}

fun Activity.hasNavBar(): Boolean {
    val display = windowManager.defaultDisplay

    val realDisplayMetrics = DisplayMetrics()
    display.getRealMetrics(realDisplayMetrics)

    val displayMetrics = DisplayMetrics()
    display.getMetrics(displayMetrics)

    return (realDisplayMetrics.widthPixels - displayMetrics.widthPixels > 0) || (realDisplayMetrics.heightPixels - displayMetrics.heightPixels > 0)
}

fun Activity.fullscreen() {
    with(WindowInsetsControllerCompat(window, window.decorView)) {
        systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        hide(WindowInsetsCompat.Type.systemBars())
    }
}

fun Activity.exitFullscreen() {
    WindowInsetsControllerCompat(
        window,
        window.decorView
    ).show(WindowInsetsCompat.Type.systemBars())
}

fun Activity.setTranslucentNavigation() {
    window.setFlags(
        WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
        WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
    )
}

fun Activity.updateStatusbarColor(color: Int) {
    window.statusBarColor = color

    if (color.getContrastColor() == DARK_GREY) {
        window.decorView.systemUiVisibility =
            window.decorView.systemUiVisibility.addBit(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
    } else {
        window.decorView.systemUiVisibility =
            window.decorView.systemUiVisibility.removeBit(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
    }
}

fun Int.addBit(bit: Int) = this or bit

fun Int.removeBit(bit: Int) = addBit(bit) - bit

fun AppCompatActivity.showSystemUIWithoutNav() {
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//        window.insetsController?.show(WindowInsets.Type.systemBars())
//    } else {
    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//    }
}

fun AppCompatActivity.hideSystemUI() {
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//        window.insetsController?.hide(WindowInsets.Type.systemBars())
//    } else {
    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
            View.SYSTEM_UI_FLAG_LOW_PROFILE or
            View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_IMMERSIVE
//    }
}

fun Activity.makeStatusBarTransparent() {
    window.apply {
        clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        statusBarColor = Color.TRANSPARENT
    }
}

fun PopupWindow.dimBehind(percent: Float) {
    val container = contentView.rootView
    val context = contentView.context
    val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val p = container.layoutParams as WindowManager.LayoutParams
    p.flags = p.flags or WindowManager.LayoutParams.FLAG_DIM_BEHIND
    p.dimAmount = percent
    wm.updateViewLayout(container, p)
}


fun Context.isDarkModeEnabled(): Boolean {
    val resources = resources
    val configuration = resources.configuration
    val currentUiMode = configuration.uiMode
    val darkMode = DARK_MODE.fromValue(
        PrefsManager.newInstance(this).getInt("darkMode", FOLLOW_SYSTEM.value())
    )
    return when (darkMode) {
        FOLLOW_SYSTEM -> currentUiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        else -> darkMode != LIGHT
    }
}

fun DARK_MODE.setDarkMode() {
    when (this) {
        FOLLOW_SYSTEM -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }
}