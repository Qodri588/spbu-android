package com.rohman.spbu.persistence.prefs

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.SharedPreferences

class SelectedBluetoothPrefs {
    private val BLUETOOTH_PREFS = "BLUETOOTH_PREFS";
    private val BLUETOOTH_NAME = "BLUETOOTH_NAME";
    private val BLUETOOTH_MAC = "BLUETOOTH_MAC";

    fun setSelectedBluetooth(context: Context, bluetoothDevice: BluetoothDevice): Boolean{
        val editor: SharedPreferences.Editor =
            context.getSharedPreferences(BLUETOOTH_PREFS, Context.MODE_PRIVATE).edit()

        return try {
            editor.putString(BLUETOOTH_NAME, bluetoothDevice.name)
            editor.putString(BLUETOOTH_MAC, bluetoothDevice.address)

            editor.apply()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun getSelectedBluetoothName(context: Context): String?{
        val sharedPreferences = context.getSharedPreferences(BLUETOOTH_PREFS, Context.MODE_PRIVATE)
        return sharedPreferences.getString(BLUETOOTH_NAME,null)
    }

    fun getSelectedBluetoothAddress(context: Context): String?{
        val sharedPreferences = context.getSharedPreferences(BLUETOOTH_PREFS, Context.MODE_PRIVATE)
        return sharedPreferences.getString(BLUETOOTH_MAC,null)
    }
}