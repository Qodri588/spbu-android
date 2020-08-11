package com.rohman.spbu.ui.home

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.jakewharton.rxbinding4.view.clicks
import com.jakewharton.rxbinding4.widget.textChanges
import com.mlsdev.rximagepicker.RxImagePicker
import com.mlsdev.rximagepicker.Sources
import com.rohman.spbu.R
import com.rohman.spbu.adapter.ProductAdapter
import com.rohman.spbu.databinding.FragmentSettingBinding
import com.rohman.spbu.ext.showLongToast
import com.rohman.spbu.ext.showShortToast
import com.rohman.spbu.model.Produk
import com.rohman.spbu.model.Template
import com.rohman.spbu.persistence.prefs.SelectedBluetoothPrefs
import com.rohman.spbu.ui.dialog.DialogConnectBluetooth
import com.tbruyelle.rxpermissions3.RxPermissions
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_setting.*
import java.util.concurrent.TimeUnit

class SettingFragment : Fragment(), ProductAdapter.Interaction,
    DialogConnectBluetooth.SettingCallback {

    private lateinit var binding: FragmentSettingBinding
    private lateinit var productViewModel: SettingViewModel
    private lateinit var disposable: CompositeDisposable
    private lateinit var rxPermission: RxPermissions
    private lateinit var adapter: ProductAdapter
    private var template: Template? = null
    private var bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private lateinit var oldMac: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false)
        val settingView = binding.root
        disposable = CompositeDisposable()
        rxPermission = RxPermissions(requireActivity())
        rxPermission.setLogging(true)

        return settingView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val selectedBluetoothPrefs = SelectedBluetoothPrefs()
        oldMac = selectedBluetoothPrefs.getSelectedBluetoothAddress(requireContext()).toString()

        binding.apply {
            buttonConnectBluetooth.setOnClickListener {
                val dialog = DialogConnectBluetooth(this@SettingFragment)
                dialog.show(childFragmentManager, "connect bt")
            }
        }

        binding.apply {
            val name = selectedBluetoothPrefs.getSelectedBluetoothName(requireContext())
            if (isBonded()) {
                val status = "Device dilipih $name"
                textStatus.text = status
            }
        }

        productViewModel =
            ViewModelProviders.of(requireActivity()).get(SettingViewModel::class.java)

        binding.apply {
            productViewModel.template?.observe(viewLifecycleOwner, Observer { data ->
                template = data
                inputNama.setText(data.nama)
                inputAlamat.setText(data.alamat)
                inputOperator.setText(data.operator)
                inputNomor.setText(data.nomor)

                disposable.add(
                    buttonChangeLogo.clicks()
                        .compose(rxPermission.ensure(Manifest.permission.READ_EXTERNAL_STORAGE))
                        .subscribe {
                            if (it) {
                                Handler(Looper.getMainLooper()).post {
                                    pickImage()
                                }

                            } else {
                                "Permission Denied".showLongToast(requireContext())
                            }
                        }
                )

                if (inputNama.isFocused) {
                    inputNama.requestFocus()
                    inputNama.setSelection(inputNama.text.length)
                }

                if (inputAlamat.isFocused) {
                    inputAlamat.requestFocus()
                    inputAlamat.setSelection(inputAlamat.text.length)
                }

                if (inputNomor.isFocused) {
                    inputNomor.requestFocus()
                    inputNomor.setSelection(inputNomor.text.length)
                }

                if (inputOperator.isFocused) {
                    inputOperator.requestFocus()
                    inputOperator.setSelection(inputOperator.text.length)
                }

                Glide.with(requireActivity()).load(data.logo.toUri())
                    .error(R.drawable.logo)
                    .into(imageLogo)

                disposable.add(inputNama.textChanges()
                    .skip(1)
                    .debounce(3, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .map { it.toString() }
                    .filter { it.isNotEmpty() }
                    .filter { it.toString() != data.nama }
                    .doOnNext {
                        val dataTemp = data
                        dataTemp.nama = it
                        productViewModel.updateTemplate(dataTemp)
                        showToast()
                    }
                    .subscribe())

                disposable.add(inputAlamat.textChanges()
                    .skip(1)
                    .debounce(3, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .map { it.toString() }
                    .filter { it.isNotEmpty() }
                    .filter { it.toString() != data.alamat }
                    .doOnNext {
                        val dataTemp = data
                        dataTemp.alamat = it
                        productViewModel.updateTemplate(dataTemp)
                        showToast()
                    }
                    .subscribe())

                disposable.add(inputNomor.textChanges()
                    .skip(1)
                    .debounce(3, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .map { it.toString() }
                    .filter { it.isNotEmpty() }
                    .filter { it.toString() != data.nomor }
                    .doOnNext {
                        val dataTemp = data
                        dataTemp.nomor = it
                        productViewModel.updateTemplate(dataTemp)
                        showToast()
                    }
                    .subscribe())

                disposable.add(inputOperator.textChanges()
                    .skip(1)
                    .debounce(3, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .map { it.toString() }
                    .filter { it.isNotEmpty() }
                    .filter { it.toString() != data.operator }
                    .doOnNext {
                        val dataTemp = data
                        dataTemp.operator = it
                        productViewModel.updateTemplate(dataTemp)
                        showToast()
                    }
                    .subscribe())
            })
        }

        binding.apply {
            productViewModel.getNotReactiveProduct()

            productViewModel.productsNotReactive.observe(viewLifecycleOwner, Observer { data ->
                if (data.isNotEmpty()) {
                    adapter = ProductAdapter(this@SettingFragment)
                    recyclerNamaProduk.adapter = adapter
                    adapter.submitList(data)
                    recyclerNamaProduk.layoutManager = LinearLayoutManager(requireContext())
                    recyclerNamaProduk.setHasFixedSize(true)
                }
            })

            buttonTambah.setOnClickListener { onAddButtonClick() }
        }
    }

    fun isBonded(): Boolean {
        val selectedBluetoothPrefs = SelectedBluetoothPrefs()
        val mac = selectedBluetoothPrefs.getSelectedBluetoothAddress(requireContext())
        val pairedDevice = bluetoothAdapter.bondedDevices
        pairedDevice.forEach {
            if (it.address == mac) {
                return true
            }
        }
        return false
    }

    private fun pickImage() {
        RxImagePicker.with(fragmentManager = childFragmentManager)
            .requestImage(Sources.GALLERY)
            .subscribeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
            .subscribe {
                Glide.with(requireActivity()).load(it).into(image_logo)
                template?.logo = it.toString()
                template?.let { it1 -> productViewModel.updateTemplate(it1) }
                "Tersimpan".showShortToast(requireContext())
            }
    }

    override fun onItemSelected(position: Int, produk: Produk) {

    }

    override fun onAddButtonClick() {
        productViewModel.insert(Produk(null))
    }

    override fun onDeleteClick(produk: Produk) {
        productViewModel.delete(produk)
    }

    override fun onNamaChanged(produk: Produk) {
        if (produk.nama != "") {
            productViewModel.update(produk)
            "Tersimpan".showShortToast(requireContext())
        }
    }

    override fun onHargaChanged(produk: Produk) {
        if (produk.harga != 0.0) {
            productViewModel.update(produk)
            "Tersimpan".showShortToast(requireContext())
        }
    }

    fun showToast() {
        "Tersimpan".showShortToast(requireActivity())
    }


    override fun onDestroy() {
        if (!disposable.isDisposed) {
            disposable.dispose()
        }
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        binding.apply {
            val selectedBluetoothPrefs = SelectedBluetoothPrefs()
            val name = selectedBluetoothPrefs.getSelectedBluetoothName(requireContext())
            if (isBonded()) {
                val status = "Device dilipih $name"
                textStatus.text = status
            }
        }
    }

    override fun onDeviceSelected() {
        val selectedBluetoothPrefs = SelectedBluetoothPrefs()
        val name = selectedBluetoothPrefs.getSelectedBluetoothName(requireContext())
        val mac = selectedBluetoothPrefs.getSelectedBluetoothAddress(requireContext())
        if (isBonded()) {
            val status = "Device dilipih $name"
            binding.apply {
                textStatus.text = status
            }
        }
        (activity as MainActivity).disconnectPrinter()
        println("DISCONNECT CALLED")

    }
}
