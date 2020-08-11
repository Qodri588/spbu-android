package com.rohman.spbu.epoxyModel

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.bumptech.glide.Glide
import com.rohman.spbu.R
import com.rohman.spbu.model.Foto

@EpoxyModelClass(layout = R.layout.view_holder_history_foto)
abstract class FotoModel : EpoxyModelWithHolder<FotoModel.Holder>() {

    @EpoxyAttribute
    lateinit var foto: Foto

    @EpoxyAttribute
    lateinit var listener: () -> Unit

    override fun bind(holder: Holder) {
        super.bind(holder)
        with(holder) {
            Glide.with(image_foto.context)
                .load(foto.foto).error(R.color.colorGray)
                .into(image_foto)
            image_foto.setOnClickListener {
                listener()
            }
            text_date.text = foto.date
        }
    }

    override fun unbind(holder: Holder) {
        super.unbind(holder)
        with(holder) {
            text_date.text = null
        }

    }

    class Holder : EpoxyHolder() {

        lateinit var image_foto: ImageView
        lateinit var text_date: TextView

        override fun bindView(itemView: View) {
            image_foto = itemView.findViewById(R.id.image_history)
            text_date = itemView.findViewById(R.id.text_date)
        }


    }

}