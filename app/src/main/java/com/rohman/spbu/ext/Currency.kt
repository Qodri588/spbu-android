package com.rohman.spbu.ext

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*


fun Double.toRoundDouble(): Double {
    val number3digits: Double = String.format("%.3f", this).toDouble()
    val number2digits: Double = String.format("%.2f", number3digits).toDouble()
    val solution: Double = String.format("%.1f", number2digits).toDouble()
    return solution
}

fun Double.toVolumeValue(): String{
    val formatter = DecimalFormat("#.###")
    return formatter.format(this)
}

fun Double.toIndonesiaCurrency(): String {
    val locale = Locale("id", "ID")
    return NumberFormat.getCurrencyInstance(locale)
        .format(this)
}

fun String.toDoublePrintFormat(): String{
    var temp = this[0].toString() + this[1].toString() + ".    "
    this.forEachIndexed {i,d ->
        if (i>1){
            temp += d.toString()
        }
    }
    return temp
}

fun String.toDoublePrintFormatWithoutRp(): String{
    var temp = ""
    this.forEachIndexed {i,d ->
        if (i>1){
            temp += d.toString()
        }
    }
    return temp
}