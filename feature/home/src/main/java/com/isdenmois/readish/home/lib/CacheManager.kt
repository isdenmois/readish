package com.isdenmois.readish.home.lib

import android.content.SharedPreferences
import javax.inject.Inject

class CacheManager @Inject constructor(private val preferences: SharedPreferences) {
    fun contains(key: String) = preferences.contains(key)

    fun get(key: String) = preferences.getString(key, null)

    fun set(key: String, value: String) {
        with(preferences.edit()) {
            putString(key, value)
            apply()
        }
    }
}
