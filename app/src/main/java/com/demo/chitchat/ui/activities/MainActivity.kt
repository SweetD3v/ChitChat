package com.demo.chitchat.ui.activities

import DARK_MODE
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.NotificationCompat
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
        showNotification()

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

    private fun showNotification() {
        val channelId = "my_channel_id"
        val channelName = "My Channel Name"
        val importance = NotificationManager.IMPORTANCE_DEFAULT

        val channel = NotificationChannel(channelId, channelName, importance)
        channel.description = "Description of my notification channel"

// Get the notification manager system service
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

// Create the notification channel
        notificationManager.createNotificationChannel(channel)

        val notificationId = 123

        val intent = Intent(this, MainActivity::class.java) // Target activity to launch
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK // Ensure app launches properly

        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("My Notification Title")
            .setContentText("This is the notification content")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

// Add other optional elements here

// Build the notification
        val notification = notificationBuilder.build()

// Show the notification
        notificationManager.notify(notificationId, notification)
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