package com.rohman.spbu.ui.historyView

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.rohman.spbu.R
import com.rohman.spbu.ext.showShortToast
import com.rohman.spbu.ext.toDoublePrintFormat
import com.rohman.spbu.ext.toDoublePrintFormatWithoutRp
import com.rohman.spbu.ext.toIndonesiaCurrency
import com.rohman.spbu.model.Manual
import com.rohman.spbu.ui.dialog.LoadingDialog
import com.rohman.spbu.ui.home.MainActivity
import com.rohman.spbu.util.convertGreyImg
import com.rohman.spbu.util.resizeImage
import kotlinx.android.synthetic.main.fragment_history_view.*
import net.posprinter.posprinterface.IMyBinder
import net.posprinter.posprinterface.ProcessData
import net.posprinter.posprinterface.UiExecute
import net.posprinter.utils.BitmapToByteData
import net.posprinter.utils.DataForSendToPrinterPos58
import java.util.ArrayList

class HistoryViewFragment : Fragment() {

    companion object {
        const val TYPE_MANUAL = "MANUAL"
        const val TYPE_FOTO = "FOTO"
    }

    private lateinit var manual: Manual

    private val viewmodel: HistoryViewViewModel by activityViewModels()
    var binder:IMyBinder? = null
    private lateinit var loadingDialog: LoadingDialog
    private var bitmapObj: Bitmap?= null
    val PAGE_WIDTH = 580
    val CONTENT_WIDTH = 380

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = arguments?.let {
            HistoryViewFragmentArgs.fromBundle(
                it
            )
        }

        binder = (activity as MainActivity).getBinder()
        loadingDialog = LoadingDialog()

        button_print.setOnClickListener {
            loadingDialog.show(childFragmentManager,"loadnnnd")
            printpicCode()
        }


        if (args?.type == TYPE_FOTO) {
            viewmodel.allFoto.observe(viewLifecycleOwner, Observer { data ->
                data.forEach {
                    if (it.id == args.id) {
                        Glide.with(requireActivity()).asBitmap().load(it.foto).into(object : SimpleTarget<Bitmap>(){
                            override fun onResourceReady(
                                resource: Bitmap,
                                transition: Transition<in Bitmap>?
                            ) {
                                bitmapObj =
                                    convertGreyImg(resource)?.let { it1 -> resizeImage(it1,CONTENT_WIDTH,false) }
                                Glide.with(requireActivity()).load(bitmapObj).into(image_preview)
                            }
                        })
                    }
                }
            })
        }else if (args?.type == TYPE_MANUAL){
            viewmodel.manual.observe(viewLifecycleOwner, Observer { data ->
                data.forEach {
                    if (it.id == args.id){
                        manual = it
                    }
                }

                bitmapObj = convertGreyImg(getBitmapFromView2())?.let { resizeImage(it,CONTENT_WIDTH,false) }
                Glide.with(requireActivity()).load(bitmapObj).into(image_preview)
            })
        }

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

        return myLayout.drawingCache
    }



    /*
    let the printer print bitmap
     */
    val callback = object : UiExecute {

        override fun onsucess() {
            "Sukses".showShortToast(requireActivity())
            this@HistoryViewFragment.loadingDialog.dismiss()
            (activity as MainActivity).onBackPressed()
        }

        override fun onfailed() {
            "Gagal".showShortToast(requireActivity())
            this@HistoryViewFragment.loadingDialog.dismiss()
        }

    }

    private fun printpicCode() {
        val data = ProcessData {
            val list: MutableList<ByteArray> =
                ArrayList()
            list.add(DataForSendToPrinterPos58.initializePrinter())
            list.add(
                DataForSendToPrinterPos58.printRasterBmp(
                    0,
                    bitmapObj,
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


}