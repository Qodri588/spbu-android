package com.rohman.spbu.epoxyController

import android.util.Log
import com.airbnb.epoxy.AsyncEpoxyController
import com.airbnb.epoxy.Typed2EpoxyController
import com.airbnb.epoxy.TypedEpoxyController
import com.rohman.spbu.epoxyModel.buttonOutline
import com.rohman.spbu.epoxyModel.produk
import com.rohman.spbu.model.Produk

class ProductController(val productCallback: ProductCallback) :
    TypedEpoxyController<List<Produk>>() {

//    var allProducts: List<Produk> = emptyList()
//        set(value) {
//            field = value
//            requestModelBuild()
//        }


//     fun buildModels() {
//        allProducts.forEach {
//            produk {
//                id(it.id)
//                produk(it)
//                hapusOnClick {
//                    if (allProducts.size > 1) {
//                        productCallback.onDeleteClick(it)
//                        requestModelBuild()
//                    }
//                }
//                namaOnChange {
//                    if (it.nama != "") {
//                        productCallback.onNamaChanged(it)
//                        requestDelayedModelBuild(100)
//                    }
//                }
//
//                hargaOnChange {
//                    if (it.harga != "") {
//                        productCallback.onHargaChanged(it)
//                        requestDelayedModelBuild(100)
//                    }
//                }
//            }
//        }
//        buttonOutline {
//            Log.d("products", allProducts.toString())
//            id("button")
//            text("Tambah")
//            listener { productCallback.onAddButtonClick() }
//        }
//    }

    interface ProductCallback {
        fun onAddButtonClick()
        fun onDeleteClick(produk: Produk)
        fun onNamaChanged(produk: Produk)
        fun onHargaChanged(produk: Produk)
    }

    override fun buildModels(data1: List<Produk>?) {
        data1?.forEach {
            produk {
                id(it.id)
                produk(it)
                hapusOnClick {
                    if (data1.size > 1) {
                        productCallback.onDeleteClick(it)
//                        requestModelBuild()
                    }
                }
                namaOnChange {
                    if (it.nama != "") {
                        productCallback.onNamaChanged(it)
//                        requestDelayedModelBuild(100)
                    }
                }

                hargaOnChange {
                    if (it.harga != 0.0) {
                        productCallback.onHargaChanged(it)
//                        requestDelayedModelBuild(100)
                    }
                }
            }
        }
        buttonOutline {
            Log.d("products", data1.toString())
            id("button")
            text("Tambah")
            listener { productCallback.onAddButtonClick() }
        }
    }

}