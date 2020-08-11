package com.rohman.spbu.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rohman.spbu.R
import com.rohman.spbu.model.Manual

class HistoryManualAdapter(private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Manual>() {

        override fun areItemsTheSame(oldItem: Manual, newItem: Manual): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Manual, newItem: Manual): Boolean {
            return oldItem == newItem
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.view_holder_manual,
                parent,
                false
            ),
            interaction
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                holder.bind(differ.currentList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<Manual>) {
        differ.submitList(list)
    }

    class ViewHolder
    constructor(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {

        val text_date = itemView.findViewById(R.id.text_date) as TextView
        val text_harga = itemView.findViewById(R.id.text_price_detail) as TextView
        val text_jumlah = itemView.findViewById(R.id.text_qty_detail) as TextView
        val button_type = itemView.findViewById(R.id.text_nama_produk) as TextView


        fun bind(item: Manual) = with(itemView) {
            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)
            }
//            text_date.text = com.rohman.spbu.utils.Converters().dateToTimestamp(item.waktu)
            text_harga.text = item.cash.toString()
            text_jumlah.text = item.total_harga.toString()
            button_type.text = item.nama
        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: Manual)
    }
}
