package com.rohman.spbu.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.rohman.spbu.R
import com.rohman.spbu.adapter.HistoryManualAdapter
import com.rohman.spbu.databinding.FragmentHistoryManualBinding
import com.rohman.spbu.epoxyModel.buttonOutline
import com.rohman.spbu.epoxyModel.manual
import com.rohman.spbu.ext.showLongToast
import com.rohman.spbu.model.Manual
import com.rohman.spbu.ui.historyView.HistoryViewFragment
import com.rohman.spbu.ui.history.HistoryManualViewModel

class HistoryManualFragment : Fragment(), HistoryManualAdapter.Interaction {

    //    private var historyAdapter: HistoryManualAdapter = HistoryManualAdapter(this)
    private lateinit var binding: FragmentHistoryManualBinding
    private val viewmodel: HistoryManualViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_history_manual, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            viewmodel.manual.observe(viewLifecycleOwner, Observer { item ->
                historyManualRecycler.withModels {
                    item.forEach { data ->
                        manual {
                            id(data.id)
                            manualObj(data)
                            listener {
                                findNavController().navigate(
                                    MainFragmentDirections.actionMainFragmentToHistoryViewFragment(data.id ?: 0,
                                    HistoryViewFragment.TYPE_MANUAL))
                            }
                        }
                    }

                    if (item.isNotEmpty()) {
                        buttonOutline {
                            id("buttonManual")
                            listener { findNavController().navigate(R.id.action_mainFragment_to_historyFragment) }
                            text("Selengkapnya")
                        }
                    }
                }
            })
        }
    }

    override fun onItemSelected(position: Int, item: Manual) {
        item.nama.showLongToast(requireContext())
    }


}