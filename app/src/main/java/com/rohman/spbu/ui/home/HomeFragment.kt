package com.rohman.spbu.ui.home

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.rohman.spbu.R
import com.rohman.spbu.adapter.HistoryAdapter
import com.rohman.spbu.adapter.HistoryManualAdapter
import com.rohman.spbu.databinding.FragmentHomeBinding
import com.rohman.spbu.model.Manual
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment(),HistoryManualAdapter.Interaction {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            adapter = HistoryAdapter(childFragmentManager)
            historyViewPager.adapter = adapter
            adapter.addFragment(HistoryManualFragment(),"Manual")
            adapter.addFragment(HistoryFotoFragment(),"Foto")
            adapter.notifyDataSetChanged()
            historyViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {}
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                }

                override fun onPageSelected(position: Int) {
                    if (position == 0) {
                        toggleButton(buttonManual)
                    } else {
                        toggleButton(buttonFoto)
                    }
                }
            })


            imageCarouselManual.setOnClickListener {
                findNavController().navigate(R.id.action_mainFragment_to_printManualFragment)
            }
            imageCarouselPhoto.setOnClickListener {
                findNavController().navigate(R.id.action_mainFragment_to_printFotoFragment)
            }


            buttonManual.setOnClickListener {
                historyViewPager.currentItem = 0
                toggleButton(buttonManual)
            }
            buttonFoto.setOnClickListener {
                historyViewPager.currentItem = 1
                toggleButton(buttonFoto)
            }
        }
    }

    private fun toggleButton(buttonId: Button) {
        if (buttonId.id == R.id.button_manual) {
            button_foto.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorWhite
                )
            )
            button_foto.setTextColor(
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorPrimary
                    )
                )
            )
        } else {
            button_manual.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorWhite
                )
            )
            button_manual.setTextColor(
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorPrimary
                    )
                )
            )
        }

        buttonId.setTextColor(
            ColorStateList.valueOf(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorWhite
                )
            )
        )
        buttonId.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorPrimary
            )
        )

    }

    override fun onResume() {
        super.onResume()
    }

    override fun onItemSelected(position: Int, item: Manual) {

    }
}