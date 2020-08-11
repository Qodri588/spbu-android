package com.rohman.spbu.ext

import java.text.SimpleDateFormat
import java.util.*

private val sdf = SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault())

fun Date.toStringDate(): String {
    return sdf.format(this)
}
