package com.pa.submissionaplikasistoryapp.data.remote.pref

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit


object UserTokenPref {
    private const val PREFS_NAME = "MyPrefs"
    private const val KEY_IS_LOGGED_IN = "isLoggedIn"
    private const val KEY_TOKEN = "token"

    private val sharedPreferences: SharedPreferences by lazy {
        MyApp.instance.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun setLoggedIn(isLoggedIn: Boolean) {
        sharedPreferences.edit {
            putBoolean(KEY_IS_LOGGED_IN, isLoggedIn)
        }
    }

    fun getToken(): String? {
        return sharedPreferences.getString(KEY_TOKEN, null)
    }

    fun setToken(token: String) {
        sharedPreferences.edit {
            putString(KEY_TOKEN, token)
        }
    }

}
