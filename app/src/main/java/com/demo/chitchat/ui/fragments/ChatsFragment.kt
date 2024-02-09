package com.demo.chitchat.ui.fragments

import android.os.Bundle
import android.view.View
import com.demo.chitchat.databinding.FragmentChatsBinding

class ChatsFragment : BaseFragment<FragmentChatsBinding>() {
    override val binding: FragmentChatsBinding by lazy { FragmentChatsBinding.inflate(layoutInflater) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }

    override fun onBackBtnPressed() {
    }

    override fun onPermissionGranted() {
    }
}
