package com.rohman.spbu.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class MainViewPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(
    fm,
    FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {
    private var listFragment = ArrayList<Fragment>()

    fun addFragment(fragment: Fragment){
        listFragment.add(fragment)
    }

    override fun getItem(position: Int): Fragment = listFragment[position]
    override fun getCount(): Int = listFragment.size

}