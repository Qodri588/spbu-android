package com.rohman.spbu.ext

import android.content.Context
import android.widget.EditText

fun EditText.isEdittextNotEmpty(context: Context): Boolean {
    if (this.text.toString().isEmpty()) {
//       "${this.hint} tidak boleh kosong".showShortToast(context)
//        this.error = "${this.hint} tidak boleh kosong"
        return false
    }
    return true
}

fun EditText.isEdittextNotEmptyWithError(context: Context): Boolean {
    if (this.text.toString().isEmpty()) {
       "${this.hint} tidak boleh kosong".showShortToast(context)
        this.error = "${this.hint} tidak boleh kosong"
        return false
    }
    return true
}