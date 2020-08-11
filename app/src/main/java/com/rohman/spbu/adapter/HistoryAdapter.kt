package com.rohman.spbu.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter


class HistoryAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(
    fm,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {

    private var listFragment = ArrayList<Fragment>()
    private var listTitle = ArrayList<String>()

    fun addFragment(fragment: Fragment,title:String){
        listFragment.add(fragment)
        listTitle.add(title)
    }

    override fun getPageTitle(position: Int): CharSequence? = listTitle[position]
    override fun getItem(position: Int): Fragment = listFragment[position]
    override fun getCount(): Int = listFragment.size


}