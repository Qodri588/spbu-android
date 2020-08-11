package com.rohman.spbu.persistence.prefs

import android.content.Context
import android.content.SharedPreferences
import com.rohman.spbu.persistence.prefs.LoginPrefInterface

class LoginPrefs: LoginPrefInterface {
    private val FIRST_OPEN_APP = "first_login";

    override fun setFirstLogin(context: Context): Boolean {
        val editor: SharedPreferences.Editor =
            context.getSharedPreferences(FIRST_OPEN_APP, Context.MODE_PRIVATE).edit()
        return try {
            editor.putBoolean(FIRST_OPEN_APP, false)
            editor.apply()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override fun getFirstLogin(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(FIRST_OPEN_APP, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(FIRST_OPEN_APP, true)
    }

}