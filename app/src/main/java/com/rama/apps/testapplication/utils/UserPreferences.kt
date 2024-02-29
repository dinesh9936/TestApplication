package com.rama.apps.testapplication.utils

import android.content.SharedPreferences
import javax.inject.Inject

class UserPreferences @Inject constructor(private val sharedPreferences: SharedPreferences) {


    companion object{
        private const val KEY_SET_AUTOPLAY = "setAutoPlay"
        private const val KEY_SET_LOGGED_IN = "isLoggedIn"
    }

    var setAutoPlay: Boolean
        get() = sharedPreferences.getBoolean(KEY_SET_AUTOPLAY, false)
        set(value) = sharedPreferences.edit().putBoolean(KEY_SET_AUTOPLAY, value).apply()

    var setLoggedIn: Boolean
        get() = sharedPreferences.getBoolean(KEY_SET_LOGGED_IN, false)
        set(value) = sharedPreferences.edit().putBoolean(KEY_SET_LOGGED_IN, value).apply()

}