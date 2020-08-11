package com.rohman.spbu.persistence.prefs

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.rohman.spbu.persistence.prefs.AccountInterface


class AccountPrefs : AccountInterface {
    private val ACCOUNT_PREFS = "ACCOUNT_PREFS";
    private val ACCESS_TOKEN_KEY = "access_token";

    override fun setAccessToken(context: Context, token: String): Boolean {
        val editor: SharedPreferences.Editor =
            context.getSharedPreferences(ACCOUNT_PREFS, MODE_PRIVATE).edit()
        return try {
            editor.putString(ACCESS_TOKEN_KEY, token)
            editor.apply()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override fun getAccessToken(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(ACCOUNT_PREFS, MODE_PRIVATE)
        return sharedPreferences.getString(ACCESS_TOKEN_KEY, null)
    }

    override fun deleteAccessToken(context: Context): Boolean {
        val editor: SharedPreferences.Editor =
            context.getSharedPreferences(ACCOUNT_PREFS, MODE_PRIVATE).edit()
        return try {
            editor.putString(ACCESS_TOKEN_KEY, null)
            editor.apply()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}