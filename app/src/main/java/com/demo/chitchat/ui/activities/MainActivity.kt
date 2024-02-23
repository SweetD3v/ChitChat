package com.demo.chitchat.ui.activities

import DARK_MODE
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.viewpager2.widget.ViewPager2
import com.demo.chitchat.R
import com.demo.chitchat.adapters.HomePagerAdapter
import com.demo.chitchat.databinding.ActivityMainBinding
import com.demo.chitchat.ui.fragments.ChatsFragment
import com.demo.chitchat.ui.fragments.GroupsFragment
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

        darkMode = DARK_MODE.LIGHT
        darkMode.setDarkMode()
        initBottomBar()
        setupHomePager()

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
                            Intent(this@MainActivity, ProfileActivity::class.java).apply {
                                startActivity(this)
                            }
                        }
                    }
                    true
                }
                popupMenu.show()
            }
        }
    }

    private fun setupHomePager() {
        binding.run {
            homePager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
            homePager.isUserInputEnabled = false
            homePager.adapter =
                HomePagerAdapter(
                    this@MainActivity, arrayListOf(
                        ChatsFragment.newInstance(),
                        GroupsFragment.newInstance()
                    )
                )
        }
    }

    private fun initBottomBar() {
        binding.run {
            btnChats.setOnClickListener {
                mBottomBar.transitionToState(R.id.scene_chats)
                mBottomBar.post {
                    homePager.setCurrentItem(0, true)
                }
            }

            btnGroups.setOnClickListener {
                mBottomBar.transitionToState(R.id.scene_group_chats)
                mBottomBar.post {
                    homePager.setCurrentItem(1, true)
                }
            }

            btnCalls.setOnClickListener {
                mBottomBar.transitionToState(R.id.scene_calls)
                mBottomBar.post {
                    homePager.setCurrentItem(2, true)
                }
            }
        }
    }
}