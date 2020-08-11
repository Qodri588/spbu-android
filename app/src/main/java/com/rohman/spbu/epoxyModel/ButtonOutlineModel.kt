package com.rohman.spbu.epoxyModel

import android.view.View
import android.widget.Button
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.rohman.spbu.R

@EpoxyModelClass(layout = R.layout.view_holder_button_outline)
abstract class ButtonOutlineModel : EpoxyModelWithHolder<ButtonOutlineModel.Holder>(){

    @EpoxyAttribute
    lateinit var listener: () -> Unit

    @EpoxyAttribute
    lateinit var text: String

    override fun unbind(holder: Holder) {
        super.unbind(holder)
        holder.button_show.setOnClickListener(null)
    }

    override fun bind(holder: Holder) {
        super.bind(holder)
        holder.button_show.setOnClickListener{listener()}
        holder.button_show.text = text
    }

    class Holder: EpoxyHolder() {
        lateinit var button_show: Button
        override fun bindView(itemView: View) {
            button_show = itemView.findViewById(R.id.button_show)
        }

    }

}