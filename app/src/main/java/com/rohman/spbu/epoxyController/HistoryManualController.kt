package com.rohman.spbu.epoxyController

import com.airbnb.epoxy.TypedEpoxyController
import com.rohman.spbu.epoxyModel.buttonOutline
import com.rohman.spbu.epoxyModel.manual
import com.rohman.spbu.model.Manual

class HistoryManualController : TypedEpoxyController<List<Manual>>() {

    override fun buildModels(item: List<Manual>) {
        item.forEach { data ->
            manual {
                id("id")
                manualObj(data)
                listener { }
            }
        }
        buttonOutline {
            id("buttonManual")
            listener { }
            text("Selengkapnya")
        }
    }

}