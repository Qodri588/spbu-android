package com.rohman.spbu.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.rohman.spbu.R
import com.rohman.spbu.adapter.MainViewPagerAdapter
import com.rohman.spbu.databinding.FragmentMainBinding
import com.tbruyelle.rxpermissions3.RxPermissions
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment(), ViewPager.OnPageChangeListener,
    BottomNavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: FragmentMainBinding
    private lateinit var mainViewPagerAdapter: MainViewPagerAdapter
    private lateinit var rxPermissions: RxPermissions
    val TAG = MainFragment::class.java.simpleName

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewPagerAdapter = MainViewPagerAdapter(childFragmentManager)
        mainViewPagerAdapter.addFragment(HomeFragment())
        mainViewPagerAdapter.addFragment(SettingFragment())
        mainViewPagerAdapter.notifyDataSetChanged()

        binding.apply {
            vpMain.adapter = mainViewPagerAdapter
            vpMain.addOnPageChangeListener(this@MainFragment)
            bottomNav.setOnNavigationItemSelectedListener(this@MainFragment)
        }

    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
        if (position == 0) {
            bottom_nav.menu.findItem(R.id.homeFragment).isChecked = true
        } else {
            bottom_nav.menu.findItem(R.id.settingFragment).isChecked = true
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.homeFragment) {
            bottom_nav.menu.findItem(R.id.homeFragment).isChecked = true
            vp_main.currentItem = 0
        } else {
            bottom_nav.menu.findItem(R.id.settingFragment).isChecked = true
            vp_main.currentItem = 1
        }
        return true
    }


}