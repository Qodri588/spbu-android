package com.rohman.spbu.persistence.prefs

import android.content.Context

interface LoginPrefInterface {
    fun setFirstLogin(context: Context): Boolean
    fun getFirstLogin(context: Context): Boolean
}