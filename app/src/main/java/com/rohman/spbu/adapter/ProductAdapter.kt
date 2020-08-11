package com.rohman.spbu.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding4.widget.textChanges
import com.rohman.spbu.R
import com.rohman.spbu.model.Produk
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit

class ProductAdapter(private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Produk>() {

        override fun areItemsTheSame(oldItem: Produk, newItem: Produk): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Produk, newItem: Produk): Boolean {
            return (oldItem.nama == newItem.nama) && (oldItem.harga == newItem.harga)
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return Holder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.view_holder_nama_produk,
                parent,
                false
            ),
            interaction
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is Holder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<Produk>) {
        differ.submitList(list)
    }

    class Holder
    constructor(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {
        private val compositeDisposable = CompositeDisposable()

        val input_nama = itemView.findViewById(R.id.input_nama_produk) as EditText
        val input_harga = itemView.findViewById(R.id.input_harga_produk) as EditText
        val button_hapus = itemView.findViewById(R.id.button_hapus) as Button

        fun bind(produk: Produk) = with(itemView) {
            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, produk)
            }

            input_nama.setText(produk.nama)
            input_harga.setText(
                produk.harga.toInt().toString()
            )

            compositeDisposable.add(input_nama.textChanges()
                .skip(1)
                .debounce(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .map { it.toString() }
                .filter { it.isNotEmpty() }
                .subscribe({
                    interaction?.onNamaChanged(
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
                    interaction?.onHargaChanged(
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

            button_hapus.setOnClickListener { interaction?.onDeleteClick(produk) }

        }
    }

    interface Interaction {
        fun onItemSelected(position: Int,produk: Produk)
        fun onAddButtonClick()
        fun onDeleteClick(produk: Produk)
        fun onNamaChanged(produk: Produk)
        fun onHargaChanged(produk: Produk)
    }
}
