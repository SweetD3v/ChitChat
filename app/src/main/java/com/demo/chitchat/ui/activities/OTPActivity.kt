package com.demo.chitchat.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.demo.chitchat.R
import com.demo.chitchat.databinding.ActivityOtpactivityBinding

class OTPActivity : AppCompatActivity() {

    private val binding by lazy { ActivityOtpactivityBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}