package com.rohman.spbu.ui.home

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.rohman.spbu.R
import com.rohman.spbu.ext.showShortToast
import com.rohman.spbu.persistence.prefs.SelectedBluetoothPrefs
import com.rohman.spbu.ui.dialog.LoadingDialog
import com.tbruyelle.rxpermissions3.RxPermissions
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_main.*
import net.posprinter.posprinterface.IMyBinder
import net.posprinter.posprinterface.UiExecute
import net.posprinter.service.PosprinterService
import net.posprinter.utils.DataForSendToPrinterPos80

class MainActivity : AppCompatActivity() {

    private lateinit var rxPermissions: RxPermissions
    private var IS_CONNECTED = false
    private var binder: IMyBinder? = null
    private lateinit var deviceName: String
    private lateinit var deviceMac: String
    private lateinit var viewmodel: MainActivityViewModel
    private lateinit var loadingDialog: LoadingDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rxPermissions = RxPermissions(this)

        initPermission()

        getBlutoothSetting()

        //bind service，get ImyBinder object
        val intent = Intent(this, PosprinterService::class.java)
        bindService(intent, conn, Context.BIND_AUTO_CREATE)
        loadingDialog = LoadingDialog()

        viewmodel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)
        viewmodel.getConnectionStatus().observe(this, Observer { data ->
            IS_CONNECTED = data
            println("BLEDEBUG VIEWMODELCALLBACK: ${data}")

            if (data) {
                deviceStatus.text = "Printer dipilih: ${deviceName}"
            } else {
                deviceStatus.text = "Tidak terhubung dengan printer"
            }

        })

    }

    private fun getBlutoothSetting() {
        val selectedBluetoothPrefs = SelectedBluetoothPrefs()
        deviceName = selectedBluetoothPrefs.getSelectedBluetoothName(this).toString()
        deviceMac = selectedBluetoothPrefs.getSelectedBluetoothAddress(this).toString()
    }

    fun getBinder(): IMyBinder? {
        return this.binder
    }

    private fun initPermission() {
        rxPermissions.request(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN
        )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { granted ->
                if (granted) {

                } else {
                    "Permission Ditolak".showShortToast(this)
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()

        unbindService(conn)

    }

    fun isConnected(): Boolean{
        return IS_CONNECTED
    }

    fun reconnectPrinter(){
            binder?.disconnectCurrentPort(object : UiExecute {
                override fun onsucess() {
                    println("BLEDEBUG DISCONNECT SUCCESS")
                    connectPrinter()
                }
                override fun onfailed() {
                    connectPrinter()
                    println("BLEDEBUG DISCONNECT FAILED")
                }
            })
    }

    fun connectPrinter() {
        loadingDialog.show(supportFragmentManager,"loadingMain")
        getBlutoothSetting()
        val bleAdrress = deviceMac
        println("BLEDEBUG THIS METHOD CALLED")

        if (bleAdrress == "") {
            viewmodel.setConnectionStatus(false)
            println("BLEDEBUG ADDRESS IS NULL $bleAdrress")
            loadingDialog.dismiss()
        } else {
            binder?.connectBtPort(bleAdrress, object : UiExecute {
                override fun onsucess() {
                    viewmodel.setConnectionStatus(true)
                    println("BLEDEBUG MAIN: CONNECTED")
                    loadingDialog.dismiss()

                    binder?.write(
                        DataForSendToPrinterPos80.openOrCloseAutoReturnPrintState(
                            0x1f
                        ), object : UiExecute {
                            override fun onsucess() {
                                binder?.acceptdatafromprinter(object : UiExecute {
                                    override fun onsucess() {
                                        viewmodel.setConnectionStatus(true)
                                        loadingDialog.dismiss()

                                    }

                                    override fun onfailed() {
                                        viewmodel.setConnectionStatus(false)
                                        println("BLEDEBUG MAIN: CONNECT FAILED")
                                        loadingDialog.dismiss()

                                    }
                                })
                            }

                            override fun onfailed() {
                                println("BLEDEBUG MAIN: FAILED 2")
                                loadingDialog.dismiss()

                            }
                        })
                }

                override fun onfailed() {
                    viewmodel.setConnectionStatus(false)
                    println("BLEDEBUG MAIN: CONNECT FAILED END")
                    loadingDialog.dismiss()

                }
            })
        }

    }

    private val conn = object : ServiceConnection {
        override fun onServiceConnected(
            componentName: ComponentName,
            iBinder: IBinder
        ) {
            //Bind successfully
            binder = iBinder as IMyBinder
            connectPrinter()
            Log.e("binder", "connected")
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            Log.e("disbinder", "disconnected")
        }
    }


}