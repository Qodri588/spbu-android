package com.rohman.spbu.persistence.prefs

import android.content.Context

interface AccountInterface{
    fun setAccessToken(context: Context, token: String): Boolean
    fun getAccessToken(context: Context) : String?
    fun deleteAccessToken(context: Context) : Boolean
}