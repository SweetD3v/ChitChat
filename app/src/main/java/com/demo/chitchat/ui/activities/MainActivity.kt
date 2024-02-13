package com.demo.chitchat.ui.activities

import DARK_MODE
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import com.demo.chitchat.R
import com.demo.chitchat.databinding.ActivityMainBinding
import com.demo.chitchat.utils.PrefsManager

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
            mBottomBar.transitionToState(R.id.scene_chats, 0)

            btnMore.setOnClickListener {
                val popupMenu = PopupMenu(this@MainActivity, it)
                popupMenu.inflate(R.menu.main_menu)
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.action_newgroup -> {

                        }

                        R.id.action_profile -> {

                        }
                    }
                    true
                }
                popupMenu.show()
            }
        }
    }

    private fun initBottomBar() {
        binding.run {
            btnChats.setOnClickListener {
                mBottomBar.transitionToState(R.id.scene_chats)
            }

            btnGroups.setOnClickListener {
                mBottomBar.transitionToState(R.id.scene_group_chats)
            }

            btnCalls.setOnClickListener {
                mBottomBar.transitionToState(R.id.scene_calls)
            }
        }
    }
}