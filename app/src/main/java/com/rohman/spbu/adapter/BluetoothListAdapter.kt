package com.rohman.spbu.adapter

import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rohman.spbu.R
import com.rohman.spbu.ext.showShortToast

class BluetoothListAdapter(private val interaction: Interaction? = null) :
    RecyclerView.Adapter<BluetoothListAdapter.ViewHolder>() {

    private var list = ArrayList<BluetoothDevice>()

    interface Interaction {
        fun onItemSelected(position: Int, item: BluetoothDevice)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text = itemView.findViewById(R.id.text_name) as TextView
    }


    fun submitLis(list: ArrayList<BluetoothDevice>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rootView = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_holder_bluetooth_list, parent, false)
        return ViewHolder(rootView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.text.text = list[position].name
        holder.itemView.setOnClickListener { interaction?.onItemSelected(position,list[position]) }
    }


}
