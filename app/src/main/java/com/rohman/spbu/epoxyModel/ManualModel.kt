package com.rohman.spbu.epoxyModel

import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.rohman.spbu.R
import com.rohman.spbu.ext.toIndonesiaCurrency
import com.rohman.spbu.model.Manual

@EpoxyModelClass(layout = R.layout.view_holder_manual)
abstract class ManualModel : EpoxyModelWithHolder<ManualModel.Holder>() {

    @EpoxyAttribute
    lateinit var manualObj: Manual

    @EpoxyAttribute
    lateinit var listener: () -> Unit

    class Holder : EpoxyHolder() {
        lateinit var text_tanggal: TextView
        lateinit var text_jumlah: TextView
        lateinit var text_harga: TextView
        lateinit var text_nama_produk: TextView
        lateinit var text_status: TextView
        lateinit var card_history: CardView

        override fun bindView(itemView: View) {
            text_harga = itemView.findViewById(R.id.text_price_detail)
            text_jumlah = itemView.findViewById(R.id.text_qty_detail)
            text_tanggal = itemView.findViewById(R.id.text_date)
            text_nama_produk = itemView.findViewById(R.id.text_nama_produk)
            text_status = itemView.findViewById(R.id.text_status)
            card_history = itemView.findViewById(R.id.cardHistory)
        }

    }

    override fun unbind(holder: Holder) {
        super.unbind(holder)
        with(holder) {
            text_tanggal.text = null
            text_jumlah.text = null
            text_harga.text = null
            text_nama_produk.text = null
            card_history.setOnClickListener(null)
        }
    }

    override fun bind(holder: Holder) {
        super.bind(holder)
        with(holder) {
            val harga   = "    : " + manualObj.total_harga.toIndonesiaCurrency()
            val volume         = "  : ${manualObj.volume}"
            val status = if (manualObj.status){"Status    : Sukses"} else {"Status    : Gagal"}
            text_status.text = status

            text_tanggal.text = manualObj.waktu
            text_jumlah.text = volume
            text_harga.text = harga
            text_nama_produk.text = manualObj.produk
            card_history.setOnClickListener { listener() }
        }
    }
}