package com.demo.chitchat

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.demo.chitchat.ui.activities.MainActivity

class GettingBroadCastData : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val newAct = Intent(context, MainActivity::class.java)
        newAct.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context!!.startActivity(newAct)
    }
}