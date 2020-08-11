package com.rohman.spbu.epoxyModel

import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.jakewharton.rxbinding4.widget.textChanges
import com.rohman.spbu.R
import com.rohman.spbu.model.Produk
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit

@EpoxyModelClass(layout = R.layout.view_holder_nama_produk)
abstract class ProdukModel : EpoxyModelWithHolder<ProdukModel.Holder>() {


    @EpoxyAttribute
    lateinit var produk: Produk

    @EpoxyAttribute
    lateinit var hapusOnClick: () -> Unit

    @EpoxyAttribute
    lateinit var namaOnChange: (produk: Produk) -> Unit

    @EpoxyAttribute
    lateinit var hargaOnChange: (produk: Produk) -> Unit

    var compositeDisposable = CompositeDisposable()


    class Holder : EpoxyHolder() {
        lateinit var input_harga: EditText
        lateinit var input_nama: EditText
        lateinit var button_hapus: Button

        override fun bindView(itemView: View) {
            input_harga = itemView.findViewById(R.id.input_harga_produk)
            input_nama = itemView.findViewById(R.id.input_nama_produk)
            button_hapus = itemView.findViewById(R.id.button_hapus)
        }
    }


    override fun bind(holder: Holder) {
        super.bind(holder)
        with(holder) {
            input_nama.setText(produk.nama)
            input_harga.setText(
                produk.harga.toString()
            )

            compositeDisposable.add(input_nama.textChanges()
                .skip(1)
                .debounce(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .map { it.toString() }
                .filter { it.isNotEmpty() }
                .subscribe({
                    namaOnChange(
                        Produk(
                            produk.id,
                            input_nama.text.toString(),
                            input_harga.text.toString().toDouble()
                        )
                    )
                }, {
                    Log.d("rxerror", it.message.toString())
                }
                )
            )

            compositeDisposable.add(input_harga.textChanges()
                .skip(1)
                .debounce(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .map { it.toString() }
                .filter { it.isNotEmpty() }
                .subscribe({
                    hargaOnChange(
                        Produk(
                            produk.id,
                            input_nama.text.toString(),
                            input_harga.text.toString().toDouble()
                        )
                    )
                }, {
                    Log.d("rxerror", it.message.toString())
                }
                )
            )

            button_hapus.setOnClickListener { hapusOnClick() }

        }
    }


    override fun unbind(holder: Holder) {
        super.unbind(holder)
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
        with(holder) {
            input_nama.text = null
            input_harga.text = null
        }
    }
}

