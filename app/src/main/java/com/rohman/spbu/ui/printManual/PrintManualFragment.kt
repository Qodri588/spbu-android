package com.rohman.spbu.ui.printManual

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.jakewharton.rxbinding4.view.clicks
import com.jakewharton.rxbinding4.view.focusChanges
import com.jakewharton.rxbinding4.widget.textChanges
import com.mlsdev.rximagepicker.RxImagePicker
import com.mlsdev.rximagepicker.Sources
import com.rohman.spbu.R
import com.rohman.spbu.databinding.FragmentPrintManualBinding
import com.rohman.spbu.ext.*
import com.rohman.spbu.model.Manual
import com.rohman.spbu.model.Produk
import com.rohman.spbu.persistence.prefs.SelectedBluetoothPrefs
import com.rohman.spbu.ui.dialog.LoadingDialog
import com.rohman.spbu.ui.home.MainActivity
import com.rohman.spbu.util.convertGreyImg
import com.rohman.spbu.util.resizeImage
import com.tbruyelle.rxpermissions3.RxPermissions
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_print_manual.*
import net.posprinter.posprinterface.IMyBinder
import net.posprinter.posprinterface.ProcessData
import net.posprinter.posprinterface.UiExecute
import net.posprinter.utils.BitmapToByteData
import net.posprinter.utils.DataForSendToPrinterPos58
import java.util.*
import java.util.concurrent.TimeUnit


class PrintManualFragment : Fragment() {

    private lateinit var binding: FragmentPrintManualBinding
    private lateinit var datePickerDialog: DatePickerDialog
    private lateinit var timeDatePicker: TimePickerDialog
    private var manual: Manual = Manual()
    private lateinit var viewmodel: PrintManualViewModel
    private val compositeDisposable = CompositeDisposable()
    private lateinit var autoCompleteAdapter: ArrayAdapter<Produk>
    private lateinit var listProduct: List<Produk>
    private lateinit var rxPermission: RxPermissions
    private lateinit var deviceName: String
    private lateinit var deviceMac: String
    private lateinit var selectedDate: String
    private lateinit var printBitmap: Bitmap
    private lateinit var loadingDialog: LoadingDialog
    private var binder: IMyBinder? = null
    val PAGE_WIDTH = 580
    val CONTENT_WIDTH = 380

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_print_manual, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rxPermission = RxPermissions(requireActivity())

        val selectedBluetoothPrefs = SelectedBluetoothPrefs()
        deviceName = selectedBluetoothPrefs.getSelectedBluetoothName(requireContext()).toString()
        deviceMac = selectedBluetoothPrefs.getSelectedBluetoothAddress(requireContext()).toString()
        loadingDialog = LoadingDialog()

        binder = (activity as MainActivity).getBinder()

        binding.apply {
            viewmodel =
                ViewModelProviders.of(requireActivity()).get(PrintManualViewModel::class.java)

            viewmodel.products.observe(viewLifecycleOwner, androidx.lifecycle.Observer { data ->

                listProduct = data
                autoCompleteAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_expandable_list_item_1,
                    listProduct
                )
                inputNamaProduk.setAdapter(autoCompleteAdapter)

            })

            compositeDisposable.add(
                inputNamaProduk.clicks()
                    .subscribe {
                        inputNamaProduk.showDropDown()
                    }
            )
            compositeDisposable.add(
                inputNamaProduk.focusChanges()
                    .skip(1)
                    .filter { inputNamaProduk.isFocused }
                    .subscribe {
                        inputNamaProduk.showDropDown()
                    }
            )
            inputNamaProduk.setOnItemClickListener { adapterView, view, i, l ->
                val item = adapterView.getItemAtPosition(i)
                if (item is Produk) {
                    Log.d(
                        "autocomplete", """
                        ${item.nama} ${item.id} ${item.harga}
                    """.trimIndent()
                    )

                    inputNamaProduk.error = null

                    val totalHargaText = inputTotalHarga.text.toString()
                    val volumetext = inputVolume.text.toString()

                    if ((totalHargaText.isNotEmpty()) && (volumetext.isNotEmpty())) {
                        inputTotalHarga.setText("")
                        inputVolume.setText("")
                    } else if (totalHargaText.isNotEmpty() && volumetext.isEmpty()) {
                        inputVolume.setText(
                            (totalHargaText
                                .toDouble() / item.harga).toVolumeValue()
                        )
                    } else if (totalHargaText.isEmpty() && volumetext.isNotEmpty()) {
                        inputTotalHarga.setText(
                            (volumetext.toDouble() * item.harga).toInt().toString()
                        )
                    }

                    inputHargaPerLiter.setText(item.harga.toString())
                }
            }


            buttonChangeLogo.setOnClickListener { changeLogo() }
            compositeDisposable.add(
                inputHargaPerLiter.textChanges()
                    .skip(1)
                    .debounce(1, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .filter { inputHargaPerLiter.isFocused }
                    .filter { it.isNotEmpty() }
                    .filter { inputVolume.text.toString().isNotEmpty() }
                    .filter { inputTotalHarga.text.toString().isEmpty() }
                    .map { it.toString().toDouble() }
                    .subscribe {
                        inputTotalHarga.setText(
                            (inputVolume.text.toString()
                                .toDouble() * it).toInt().toString()
                        )
                    }
            )

            compositeDisposable.add(
                inputHargaPerLiter.textChanges()
                    .skip(1)
                    .debounce(1, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .filter { inputHargaPerLiter.isFocused }
                    .filter { it.isNotEmpty() }
                    .filter { inputTotalHarga.text.toString().isNotEmpty() }
                    .filter { inputVolume.text.toString().isEmpty() }
                    .map { it.toString().toDouble() }
                    .subscribe {
                        inputVolume.setText(
                            (inputTotalHarga.text.toString()
                                .toDouble() / it).toVolumeValue()
                        )
                    }
            )


            compositeDisposable.add(
                inputVolume.textChanges()
                    .skip(1)
                    .debounce(1, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .filter { inputVolume.isFocused }
                    .filter { it.isNotEmpty() }
                    .filter { inputHargaPerLiter.text.toString().isNotEmpty() }
                    .filter { inputTotalHarga.text.toString().isEmpty() }
                    .map { it.toString().toDouble() }
                    .subscribe {
                        inputTotalHarga.setText(
                            (it * inputHargaPerLiter.text.toString().toDouble()).toInt().toString()
                        )
                    }
            )

            compositeDisposable.add(
                inputVolume.textChanges()
                    .skip(1)
                    .debounce(1, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .filter { inputVolume.isFocused }
                    .filter { it.isNotEmpty() }
                    .filter { inputTotalHarga.text.toString().isNotEmpty() }
                    .filter { inputHargaPerLiter.text.toString().isEmpty() }
                    .map { it.toString().toDouble() }
                    .subscribe {
                        inputHargaPerLiter.setText(
                            (inputTotalHarga.text.toString()
                                .toDouble() / it).toInt().toString()
                        )
                    }
            )

            compositeDisposable.add(
                inputTotalHarga.textChanges()
                    .skip(1)
                    .debounce(1, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .filter { inputTotalHarga.isFocused }
                    .filter { it.isNotEmpty() }
                    .filter { inputVolume.text.toString().isNotEmpty() }
                    .filter { inputHargaPerLiter.text.toString().isEmpty() }
                    .map { it.toString().toDouble() }
                    .subscribe {
                        inputHargaPerLiter.setText(
                            (it / inputVolume.text.toString().toDouble()).toInt().toString()
                        )
                    }
            )

            compositeDisposable.add(
                inputTotalHarga.textChanges()
                    .skip(1)
                    .debounce(1, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .filter { inputTotalHarga.isFocused }
                    .filter { it.isNotEmpty() }
                    .filter { inputHargaPerLiter.text.toString().isNotEmpty() }
                    .filter { inputVolume.text.toString().isEmpty() }
                    .map { it.toString().toDouble() }
                    .subscribe {
                        inputVolume.setText(
                            (it / inputHargaPerLiter.text.toString().toDouble()).toVolumeValue()
                        )
                    }
            )

            compositeDisposable.add(
                inputTotalHarga.textChanges()
                    .map { it.toString() }
                    .subscribe {
                        inputCash.setText(it)
                    }
            )

            viewmodel.template.observe(viewLifecycleOwner, androidx.lifecycle.Observer { data ->
                manual.logo = data.logo
                manual.nama = data.nama
                manual.operator = data.operator
                manual.nomor = data.nomor
                manual.alamat = data.alamat

                Glide.with(requireActivity()).load(data.logo.toUri())
                    .error(R.drawable.logo)
                    .into(imageLogo)

                inputAlamat.setText(data.alamat)
                inputNomor.setText(data.nomor)
                inputNama.setText(data.nama)
                inputOperator.setText(data.operator)
            })

        }

        val now: Calendar = Calendar.getInstance()
        datePickerDialog = DatePickerDialog.newInstance(
            { _, year, monthOfYear, dayOfMonth ->

                var monthOfYearString = (monthOfYear + 1).toString()
                var dayOfMonthString = dayOfMonth.toString()

                if (monthOfYearString.length ==1 ){
                    monthOfYearString = "0${monthOfYearString}"
                }

                if (dayOfMonthString.length == 1){
                    dayOfMonthString = "0${dayOfMonthString}"
                }

                val date =
                    "$dayOfMonthString/$monthOfYearString/$year"
                Log.d("Datee", date)
                timeDatePicker.show(childFragmentManager, "Time Picker")
                selectedDate = date
            },
            now.get(Calendar.YEAR),
            now.get(Calendar.MONTH),
            now.get(Calendar.DAY_OF_MONTH)
        )

        timeDatePicker = TimePickerDialog.newInstance(
            { _, hourOfDay, minute, second ->
                var minuteString = minute.toString()
                var hourString = hourOfDay.toString()
                var secondString = second.toString()

                if (minuteString.length == 1){
                    minuteString = "0$minuteString"
                }
                if (hourString.length == 1){
                    hourString = "0$hourString"
                }
                if (secondString.length == 1){
                    secondString = "0${secondString}"
                }

                val date =
                    "$hourString:$minuteString:$secondString"
                Log.d("Datee", date)
                selectedDate += " $date"
                manual.waktu = date
                input_waktu_transaksi.setText(selectedDate)
            }, now.get(Calendar.HOUR_OF_DAY),
            now.get(Calendar.MINUTE), now.get(Calendar.SECOND), true
        )

        datePickerDialog.setOnCancelListener { datePickerDialog.dismiss() }
        timeDatePicker.setOnCancelListener { timeDatePicker.dismiss() }

        binding.apply {
            Glide.with(requireContext()).load(R.drawable.logo).into(imageLogo)

            inputWaktuTransaksi.setText(Date().toStringDate())
            manual.waktu = Date().toStringDate()

            inputWaktuTransaksi.setOnFocusChangeListener { view, b ->
                if (view.isFocused) {
                    datePickerDialog.show(childFragmentManager, "Date Picker")
                }
            }
        }


        binding.apply {
            buttonPrint.setOnClickListener {
                if (isInputValidWithError()) {
                    fillManual()
                    loadingDialog.show(childFragmentManager, "loading...")
                    printBT()
                }
            }
        }

        binding.apply {
            if (isInputValid()) {
                fillManual()

                Glide.with(requireActivity())
                    .load(
                        convertGreyImg(getBitmapFromView2())?.let { resizeImage(it, CONTENT_WIDTH, false) }
                    )
                    .into(imagePreview)
            }
        }

        binding.apply {
            buttonRefresh.setOnClickListener {
                if (isInputValid()) {
                    fillManual()
                }
                Glide.with(requireActivity())
                    .load(
                        convertGreyImg(getBitmapFromView2())?.let {
                            resizeImage(
                                it,
                                CONTENT_WIDTH,
                                false
                            )
                        }
                    )
                    .into(imagePreview)
            }
        }

    }

    fun fillManual() {
        binding.apply {
            manual.nomor = inputNomor.text.toString()
            manual.nama = inputNama.text.toString()
            manual.alamat = inputAlamat.text.toString()

            manual.shift = inputShift.text.toString().toInt()
            manual.no_transaksi = inputNoTransaksi.text.toString().toInt()
            manual.waktu = inputWaktuTransaksi.text.toString()

            manual.pompa = inputPompa.text.toString().toInt()
            manual.produk = inputNamaProduk.text.toString()
            manual.harga_per_liter = inputHargaPerLiter.text.toString().toDouble()
            manual.volume = inputVolume.text.toString().replace(',','.').toDouble()
            manual.total_harga = inputTotalHarga.text.toString().toDouble()
            manual.operator = inputOperator.text.toString()

            manual.cash = inputCash.text.toString().toDouble()
            manual.odometer = inputOdometer.text.toString()
            manual.no_plat = inputNomrPlat.text.toString()

        }
    }

    private fun printBT() {
        val message = Message()
        message.what = 2
        handler.handleMessage(message)
    }

    fun isInputValid(): Boolean {
        binding.apply {
            return inputHargaPerLiter.isEdittextNotEmpty(requireActivity())
                    && inputCash.isEdittextNotEmpty(requireActivity())
                    && inputTotalHarga.isEdittextNotEmpty(requireActivity())
                    && inputVolume.isEdittextNotEmpty(requireActivity())
                    && inputPompa.isEdittextNotEmpty(requireActivity())
                    && inputNoTransaksi.isEdittextNotEmpty(requireActivity())
                    && inputShift.isEdittextNotEmpty(requireActivity())
                    && inputNamaProduk.isEdittextNotEmpty(requireActivity())
        }
    }

    fun isInputValidWithError(): Boolean {
        binding.apply {
            return inputHargaPerLiter.isEdittextNotEmptyWithError(requireActivity())
                    && inputCash.isEdittextNotEmptyWithError(requireActivity())
                    && inputTotalHarga.isEdittextNotEmptyWithError(requireActivity())
                    && inputVolume.isEdittextNotEmptyWithError(requireActivity())
                    && inputPompa.isEdittextNotEmptyWithError(requireActivity())
                    && inputNoTransaksi.isEdittextNotEmptyWithError(requireActivity())
                    && inputShift.isEdittextNotEmptyWithError(requireActivity())
                    && inputNamaProduk.isEdittextNotEmptyWithError(requireActivity())
        }
    }


    /*
    let the printer print bitmap
     */
    val callback = object : UiExecute {

        override fun onsucess() {
            "Sukses".showShortToast(requireActivity())
            manual.status = true
            viewmodel.insert(manual)
            this@PrintManualFragment.loadingDialog.dismiss()
            (activity as MainActivity).onBackPressed()
        }

        override fun onfailed() {
            "Gagal".showShortToast(requireActivity())
            viewmodel.insert(manual)
            this@PrintManualFragment.loadingDialog.dismiss()
            (activity as MainActivity).onBackPressed()
        }

    }

    private fun printpicCode(printBmp: Bitmap) {
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

    var handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                1 -> {

                }
                2 -> {
                    convertGreyImg(getBitmapFromView2())?.let {
                        resizeImage(
                            it,
                            CONTENT_WIDTH, false
                        )?.let { that -> printpicCode(that) }
                    }
                }
                3 -> {

                }
                4 -> {
                }
            }
        }
    }

    private fun changeLogo() {
        RxImagePicker.with(fragmentManager = childFragmentManager)
            .requestImage(Sources.GALLERY)
            .subscribeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
            .subscribe({ data ->
                Glide.with(requireActivity()).load(data)
                    .error(R.drawable.logo)
                    .into(image_logo)
                manual.logo = data.toString()
            }, { err ->
                Log.d("changelogo", err.message.toString())
            })
    }


    fun getBitmapFromView2(): Bitmap {
        val inflatedFrame: View = layoutInflater.inflate(R.layout.print_preview_layout, null)
        val myLayout =
            inflatedFrame.findViewById<View>(R.id.previewContainer) as RelativeLayout

        val type = ResourcesCompat.getFont(requireContext(), R.font.lettergothicstd)
        val typeBold = ResourcesCompat.getFont(requireContext(), R.font.lettergothicstd_bold)

        val headerImage = myLayout.findViewById(R.id.headerImage) as ImageView
        val headerNomor = myLayout.findViewById(R.id.headerNomor) as TextView
        val headerAlamat = myLayout.findViewById(R.id.headerAlamat) as TextView
        val headerJalan = myLayout.findViewById(R.id.headerJalan) as TextView
        val headerShift = myLayout.findViewById(R.id.headerShift) as TextView
        val headerNoTrans = myLayout.findViewById(R.id.headerNoTrans) as TextView
        val headerWaktu = myLayout.findViewById(R.id.headerWaktu) as TextView
        val bodyPompa = myLayout.findViewById(R.id.bodyPompa) as TextView
        val bodyVolume = myLayout.findViewById(R.id.bodyVolume) as TextView
        val bodyNamaProduk = myLayout.findViewById(R.id.bodyNamaProduk) as TextView
        val bodyHargaPerLiter = myLayout.findViewById(R.id.bodyHargaPerLiter) as TextView
        val bodyTotalHarga = myLayout.findViewById(R.id.bodyTotalHarga) as TextView
        val bodyOperator = myLayout.findViewById(R.id.bodyOperator) as TextView
        val footerCashValue = myLayout.findViewById(R.id.footerCashValue) as TextView
        val footerNoPlat = myLayout.findViewById(R.id.footerNoPlat) as TextView
        val footerOdometer = myLayout.findViewById(R.id.footerOdometer) as TextView
        val terimakasih = myLayout.findViewById(R.id.terimakasih) as TextView
        val selamatJalan = myLayout.findViewById(R.id.selamatJalan) as TextView
        val footerCash = myLayout.findViewById(R.id.footerCash) as TextView

        val footerBatas2 = myLayout.findViewById(R.id.footerBatas2) as TextView

        headerNomor.typeface = typeBold
        headerAlamat.typeface = type
        headerJalan.typeface = type
        headerShift.typeface = type
        headerNoTrans.typeface = type
        headerWaktu.typeface = type
        bodyPompa.typeface = type
        bodyVolume.typeface = type
        bodyNamaProduk.typeface = type
        bodyHargaPerLiter.typeface = type
        bodyTotalHarga.typeface = type
        bodyOperator.typeface = type
        footerCashValue.typeface = type
        footerNoPlat.typeface = type
        footerOdometer.typeface = type
        terimakasih.typeface = type
        selamatJalan.typeface = type
        footerCash.typeface = type

        println(manual.toString())
        with(manual) {

            if (logo != "") {
                Glide.with(requireActivity()).load(logo.toUri())
                    .into(headerImage)
            }

            headerNomor.text = nomor
            headerAlamat.text = nama
            headerJalan.text = alamat
            headerShift.text = "Shift: " + shift.toString()
            headerNoTrans.text = "No.  Trans: " + no_transaksi.toString()
            headerWaktu.text = "Waktu: " + waktu
            bodyPompa.text = "Pulau/Pompa  : " + pompa.toString()
            bodyNamaProduk.text = "Nama Produk  : " + produk
            bodyVolume.text = "Volume       : (L)    " + volume
            bodyHargaPerLiter.text =
                "Harga/Liter  : " + harga_per_liter.toIndonesiaCurrency().toDoublePrintFormat()
            bodyTotalHarga.text =
                "Total Harga  : " + total_harga.toIndonesiaCurrency().toDoublePrintFormat()
            bodyOperator.text = "Operator     : " + operator
            footerCashValue.text = cash.toIndonesiaCurrency().toDoublePrintFormatWithoutRp()
            footerNoPlat.text = "No. Plat: " + no_plat
            footerOdometer.text = "Odometer: " + odometer

            if (odometer.isEmpty()){
                footerOdometer.visibility = View.GONE
            }
            if (no_plat.isEmpty()){
                footerNoPlat.visibility = View.GONE
            }

            if (no_plat.isEmpty() && odometer.isEmpty()){
                footerBatas2.visibility = View.GONE
            }

        }

        myLayout.isDrawingCacheEnabled = true
        myLayout.measure(
            View.MeasureSpec.makeMeasureSpec(
                0,
                View.MeasureSpec.UNSPECIFIED
            ),
            View.MeasureSpec.makeMeasureSpec(
                0,
                View.MeasureSpec.UNSPECIFIED
            )
        )
        myLayout.layout(0, 0, myLayout.getMeasuredWidth(), myLayout.getMeasuredHeight())
        myLayout.buildDrawingCache(true)
        printBitmap = myLayout.drawingCache

        return myLayout.drawingCache
    }



    override fun onDestroy() {
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }

        super.onDestroy()
    }

}