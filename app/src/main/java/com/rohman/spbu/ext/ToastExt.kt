package com.rohman.spbu.ext

import android.content.Context
import android.widget.Toast

fun String.showLongToast(context: Context){
    Toast.makeText(context,this,Toast.LENGTH_LONG).show()
}

fun String.showShortToast(context: Context){
    Toast.makeText(context,this,Toast.LENGTH_SHORT).show()
}