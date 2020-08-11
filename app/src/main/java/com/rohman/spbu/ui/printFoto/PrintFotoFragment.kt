package com.rohman.spbu.ui.printFoto

import android.Manifest
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.mlsdev.rximagepicker.RxImagePicker
import com.mlsdev.rximagepicker.Sources
import com.rohman.spbu.R
import com.rohman.spbu.ext.showLongToast
import com.rohman.spbu.ext.showShortToast
import com.rohman.spbu.ext.toStringDate
import com.rohman.spbu.model.Foto
import com.rohman.spbu.persistence.prefs.SelectedBluetoothPrefs
import com.rohman.spbu.ui.dialog.LoadingDialog
import com.rohman.spbu.ui.home.MainActivity
import com.rohman.spbu.util.convertGreyImg
import com.rohman.spbu.util.resizeImage
import com.rohman.spbu.ui.history.HistoryFotoViewModel
import com.tbruyelle.rxpermissions3.RxPermissions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_print_foto.*
import net.posprinter.posprinterface.IMyBinder
import net.posprinter.posprinterface.ProcessData
import net.posprinter.posprinterface.UiExecute
import net.posprinter.utils.BitmapToByteData
import net.posprinter.utils.DataForSendToPrinterPos58
import java.util.*


class PrintFotoFragment : Fragment() {

    lateinit var compositeDisposable: CompositeDisposable
    lateinit var rxPermissions: RxPermissions
    private var printingMethod: String? = null
    private lateinit var deviceName: String
    private lateinit var deviceMac: String
    var foto = Foto()
    val viewmodel: HistoryFotoViewModel by activityViewModels()
    private lateinit var selectedBitmap: Bitmap
    private lateinit var loadingDialog: LoadingDialog
    private var binder: IMyBinder? = null
    val PAGE_WIDTH = 580
    val CONTENT_WIDTH = 380


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_print_foto, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        compositeDisposable = CompositeDisposable()
        rxPermissions = RxPermissions(requireActivity())
        button_print.visibility = View.GONE
        button_change_foto.visibility = View.GONE
        compositeDisposable.add(getPermissionAdnPickImage())
        button_change_foto.setOnClickListener {
            compositeDisposable.add(getPermissionAdnPickImage())
        }

        rxPermissions = RxPermissions(this)

        loadingDialog =
            LoadingDialog()
        loadingDialog.show(childFragmentManager, "loading")


        val selectedBluetoothPrefs = SelectedBluetoothPrefs()
        deviceName = selectedBluetoothPrefs.getSelectedBluetoothName(requireContext()).toString()
        deviceMac = selectedBluetoothPrefs.getSelectedBluetoothAddress(requireContext()).toString()


        binder = (activity as MainActivity).getBinder()

        button_print.setOnClickListener {
            printFoto()
        }

    }

    private fun printFoto() {
        loadingDialog.show(childFragmentManager, "loading")
        val message = Message()
        message.what = 2
        handler.handleMessage(message)
    }

    var handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                1 -> {

                }
                2 -> {
                    convertGreyImg(selectedBitmap)?.let { resizeImage(it,CONTENT_WIDTH,false)?.let {img -> startPrint(img) } }
                }
                3 -> {

                }
                4 -> {
                }
            }
        }
    }

    val callback = object : UiExecute {

        override fun onsucess() {
            foto.status = true
            viewmodel.insert(foto)
            "Sukses".showShortToast(requireActivity())
            this@PrintFotoFragment.loadingDialog.dismiss()
            (activity as MainActivity).onBackPressed()
        }

        override fun onfailed() {
            viewmodel.insert(foto)
            "Gagal.".showShortToast(requireActivity())
            this@PrintFotoFragment.loadingDialog.dismiss()
            (activity as MainActivity).onBackPressed()
        }
    }

    private fun startPrint(printBmp: Bitmap) {
        val data = ProcessData {
            val list: MutableList<ByteArray> =
                ArrayList()
            list.add(DataForSendToPrinterPos58.initializePrinter())
            list.add(
                DataForSendToPrinterPos58.printRasterBmp(
                    0,
                    printBmp,
                    BitmapToByteData.BmpType.Threshold,
                    BitmapToByteData.AlignType.Center,
                    PAGE_WIDTH
                )
            )
            list
        }
        println("PROCESS DATA")
        println(data.processDataBeforeSend())
        binder?.writeDataByYouself(callback, data)
    }


    private fun getPermissionAdnPickImage() =
        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE)
            .subscribe {
                if (it) {
                    pickImage()
                } else {
                    "Permission Denied".showLongToast(requireContext())
                }
            }


    override fun onDestroy() {
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
        super.onDestroy()
    }

    private fun pickImage() {
        RxImagePicker.with(fragmentManager = childFragmentManager)
            .requestImage(Sources.GALLERY)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Glide.with(this)
                    .asBitmap()
                    .load(it)
                    .into(object : SimpleTarget<Bitmap?>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap?>?
                        ) {
                            selectedBitmap = resource
                            Glide.with(this@PrintFotoFragment)
                                .load(convertGreyImg(resource)).into(imageFoto)

                        }
                    })

                button_print.visibility = View.VISIBLE
                button_change_foto.visibility = View.VISIBLE
                foto.foto = it.toString()
                foto.date = Date().toStringDate()

                loadingDialog.dismiss()
            }
    }



}