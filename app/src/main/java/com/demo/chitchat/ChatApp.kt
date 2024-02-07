package com.demo.chitchat

import DARK_MODE
import DARK_MODE.DARK
import DARK_MODE.FOLLOW_SYSTEM
import DARK_MODE.LIGHT
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import com.demo.chitchat.utils.PrefsManager

class ChatApp : MultiDexApplication() {

    private val darkMode: DARK_MODE
        get() = DARK_MODE.fromValue(
            PrefsManager.newInstance(this).getInt("darkMode", FOLLOW_SYSTEM.value())
        )

    override fun onCreate() {
        super.onCreate()

        mInstance = this

        when (darkMode) {
            FOLLOW_SYSTEM -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }

    companion object {
        var mInstance: ChatApp? = null
        var isShowingAnyAd = false

        @Synchronized
        fun getInstance(): ChatApp {
            return mInstance ?: ChatApp()
        }
    }
}