package com.rohman.spbu.ui.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.rohman.spbu.R
import com.rohman.spbu.epoxyModel.manualAll
import com.rohman.spbu.model.Manual
import com.rohman.spbu.ui.historyView.HistoryViewFragment
import kotlinx.android.synthetic.main.fragment_history_manual_all.*

class HistoryManualAllFragment : Fragment(){

    val viewmodel: ManualViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val arrChecked = ArrayList<Manual>()
        setVisibilityFab(false)

        floatingActionButton.setOnClickListener {
            arrChecked.forEach {
                viewmodel.delete(it)
            }
            arrChecked.clear()
            setVisibilityFab(false)
        }

        viewmodel.allManual.observe(viewLifecycleOwner, Observer { data ->
            recyclerHistoryManualAll.withModels {
            data.forEach { item ->
              manualAll {
                  id(item.id)
                  manualObj(item)
                  listener {
                      findNavController().navigate(HistoryFragmentDirections.actionHistoryFragmentToHistoryViewFragment(item.id ?: 0,
                          HistoryViewFragment.TYPE_MANUAL))
                  }
                  checkboxListener { b ->
                        if (b){
                                arrChecked.add(item)
                        }else {
                            var index: Int? = null
                            arrChecked.forEachIndexed { i, d ->
                                if (d.id == item.id) {
                                    index = i
                                }
                            }
                            index?.let { it1 -> arrChecked.removeAt(it1) }
                        }

                      setVisibilityFab(arrChecked.size != 0)
                      println("ARRCHECKED $arrChecked")
                  }
              }
            }}
        })

    }


    fun setVisibilityFab(state: Boolean){
        if (state){
            floatingActionButton.visibility = View.VISIBLE
        }else{
            floatingActionButton.visibility = View.GONE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history_manual_all,container,false)
    }
}