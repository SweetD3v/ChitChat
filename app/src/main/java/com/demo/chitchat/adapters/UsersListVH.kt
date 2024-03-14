package com.demo.chitchat.adapters

import androidx.recyclerview.widget.RecyclerView
import com.demo.chitchat.databinding.ListItemChatsBinding
import com.demo.chitchat.models.UserPojo

class UsersListVH(private val binding: ListItemChatsBinding) :
    RecyclerView.ViewHolder(binding.root) {

        fun bind(user: UserPojo) {
        binding.run {
            txtChatTitle.text = user.name


        }
    }
}