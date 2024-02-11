package com.demo.chitchat.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.demo.chitchat.R
import com.demo.chitchat.databinding.ActivityMainBinding
import com.demo.chitchat.utils.PrefsManager
import com.demo.chitchat.utils.setDarkMode

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val prefs by lazy { PrefsManager.newInstance(this) }

    private var darkMode
        set(value) {
            prefs.putInt("darkMode", value.value())
        }
        get() = DARK_MODE.fromValue(
            prefs.getInt("darkMode", DARK_MODE.FOLLOW_SYSTEM.value())
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

//        darkMode = DARK_MODE.DARK
//        darkMode.setDarkMode()
        initBottomBar()

        binding.run {
            mlBottomBar.transitionToState(R.id.scene_chats, 0)
        }
    }

    private fun initBottomBar() {
        binding.run {
            btnChats.setOnClickListener {
                mlBottomBar.transitionToState(R.id.scene_chats)
            }

            btnGroups.setOnClickListener {
                mlBottomBar.transitionToState(R.id.scene_group_chats)
            }

            btnCalls.setOnClickListener {
                mlBottomBar.transitionToState(R.id.scene_calls)
            }
        }
    }
}