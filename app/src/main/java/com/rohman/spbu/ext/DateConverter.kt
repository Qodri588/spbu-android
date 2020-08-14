package com.rohman.spbu.ext

import java.text.SimpleDateFormat
import java.util.*

private val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())

fun Date.toStringDate(): String {
    return sdf.format(this)
}
