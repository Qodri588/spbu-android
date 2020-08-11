package com.rohman.spbu.ui.dialog

import android.Manifest
import android.app.Dialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.rohman.spbu.R
import com.rohman.spbu.adapter.BluetoothListAdapter
import com.rohman.spbu.ext.showShortToast
import com.rohman.spbu.persistence.prefs.SelectedBluetoothPrefs
import com.rohman.spbu.ui.home.SettingFragment
import com.tbruyelle.rxpermissions3.RxPermissions
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_dialog_connect_bluetooth.*
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class DialogConnectBluetooth(val settingCallback: SettingCallback) : DialogFragment(), BluetoothListAdapter.Interaction {

    private lateinit var compositeDisposable: CompositeDisposable
    private lateinit var rxPermissions: RxPermissions
    private val bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    val TAG = DialogConnectBluetooth::class.java.simpleName
    private lateinit var recAdapter: BluetoothListAdapter
    private lateinit var list: ArrayList<BluetoothDevice>
    private var bondStatus: Boolean = false

    val REQUEST_ENABLE_BT = 101

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dialog_connect_bluetooth, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        compositeDisposable = CompositeDisposable()
        rxPermissions = RxPermissions(this)

        bluetoothAdapter.startDiscovery()

        initRecyclerView()

        // Register for broadcasts when a device is discovered.
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        requireActivity().registerReceiver(receiver, filter)


        rxPermissions.request(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN
        )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { granted ->
                if (granted) {
                    connectToBluetooth()
                } else {
                    "Permission Ditolak".showShortToast(requireActivity())
                }
            }

    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when(intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)

                    println("FOUND : " + device?.name)

                    if (device != null) {
                        var unique = true
                        list.forEach {
                            if (device.address == it.address){
                                unique = false
                            }
                        }
                        if (unique) {
                            list.add(device)
                            recAdapter.submitLis(list)
                        }
                    }
                }

            }
        }
    }

    private fun initRecyclerView() {
        recAdapter = BluetoothListAdapter(this)

        recyclerListBluetooth.layoutManager = LinearLayoutManager(requireContext())
        recyclerListBluetooth.setHasFixedSize(true)
        recyclerListBluetooth.adapter = recAdapter
    }

    private fun connectToBluetooth() {
        if (isBluetoothEnable()) {
            val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter.bondedDevices

           list = ArrayList()

            pairedDevices?.forEach { device ->
                list.add(device)

            }
            recAdapter.submitLis(list)

        }
    }



    private fun isBluetoothEnable(): Boolean {
        val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        return if (mBluetoothAdapter == null) {
            "Tidak Support Bluetooth".showShortToast(requireContext())
            false
        } else if (!mBluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            "Hidupkan bluetooth terlebih dahulu".showShortToast(requireContext())
            dismiss()
            false
        } else {
            true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().unregisterReceiver(receiver)
    }

    override fun onStart() {
        super.onStart()
        val dialog: Dialog? = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window?.setLayout(width, height)
            dialog.setTitle("Pilih Device")
        }
    }

    override fun onItemSelected(position: Int, item: BluetoothDevice) {
        Log.d(TAG,item.name)
        val selectedBluetoothPrefs = SelectedBluetoothPrefs()
        val pairedDevice = bluetoothAdapter.bondedDevices
        var isPaired = false
        pairedDevice.forEach { device ->
            if (device.address == item.address){
                isPaired = true
            }
        }
        if (!isPaired){
            bondStatus = item.createBond()
        }
        selectedBluetoothPrefs.setSelectedBluetooth(requireActivity(),item)
        dismiss()
        settingCallback.onDeviceSelected()

    }

    interface SettingCallback{
        fun onDeviceSelected()
    }

}