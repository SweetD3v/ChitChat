package com.demo.chitchat.adapters

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class HomePagerAdapter(
    activity: AppCompatActivity,
    private val fragments: ArrayList<Fragment> = arrayListOf()
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}