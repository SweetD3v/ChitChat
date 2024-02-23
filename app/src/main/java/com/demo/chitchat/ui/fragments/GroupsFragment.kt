package com.demo.chitchat.ui.fragments

import android.os.Bundle
import android.view.View
import com.demo.chitchat.databinding.FragmentGroupsBinding

class GroupsFragment : BaseFragment<FragmentGroupsBinding>() {
    override val binding: FragmentGroupsBinding by lazy {
        FragmentGroupsBinding.inflate(
            layoutInflater
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }

    override fun onBackBtnPressed() {
    }

    override fun onPermissionGranted() {
    }

    companion object {
        fun newInstance(): GroupsFragment {
            val fragment = GroupsFragment()
            return fragment
        }
    }
}
