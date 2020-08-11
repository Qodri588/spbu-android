package com.rohman.spbu.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.rohman.spbu.R
import com.rohman.spbu.adapter.HistoryAdapter
import com.rohman.spbu.databinding.FragmentHistoryBinding

class HistoryFragment : Fragment() {

    private lateinit var binding: FragmentHistoryBinding
    private lateinit var adapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_history, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        (activity as MainActivity).setBottomNavVisibility(false)

        adapter = HistoryAdapter(childFragmentManager)
        adapter.addFragment(HistoryManualAllFragment(),"Manual")
        adapter.addFragment(HistoryFotoAllFragment(),"Foto")
        adapter.notifyDataSetChanged()
        binding.apply {
            historyViewPager.adapter = adapter
            tabLayout.setupWithViewPager(historyViewPager)
        }

    }
}