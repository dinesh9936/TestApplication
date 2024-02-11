package com.rama.apps.testapplication.utils

import android.content.SharedPreferences
import javax.inject.Inject

class UserPreferences @Inject constructor(private val sharedPreferences: SharedPreferences) {


    companion object{
        private const val KEY_SET_AUTOPLAY = "setAutoPlay"
    }

    var setAutoPlay: Boolean
        get() = sharedPreferences.getBoolean(KEY_SET_AUTOPLAY, false)
        set(value) = sharedPreferences.edit().putBoolean(KEY_SET_AUTOPLAY, value).apply()

}