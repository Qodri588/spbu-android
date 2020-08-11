package com.rohman.spbu.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.rohman.spbu.R
import com.rohman.spbu.epoxyModel.buttonOutline
import com.rohman.spbu.epoxyModel.foto
import com.rohman.spbu.ui.historyView.HistoryViewFragment
import com.rohman.spbu.ui.history.HistoryFotoViewModel
import kotlinx.android.synthetic.main.fragment_history_foto.*


class HistoryFotoFragment : Fragment() {
    private val viewmodel: HistoryFotoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history_foto, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewmodel.foto.observe(viewLifecycleOwner, Observer { data ->
            recyclerHistoryFoto.withModels {
                data.forEach {
                    foto {
                        id(it.id)
                        foto(it)
                        listener {
                            findNavController().navigate(
                                MainFragmentDirections.actionMainFragmentToHistoryViewFragment(it.id ?: 0,
                                    HistoryViewFragment.TYPE_FOTO))
                        }
                    }
                }
                if (data.isNotEmpty()) {
                    buttonOutline {
                        id("button outline")
                        listener {
                            findNavController().navigate(R.id.action_mainFragment_to_historyFragment)
                        }
                        text("Selengkapnya")
                    }
                }
            }
        }
        )
    }
}
