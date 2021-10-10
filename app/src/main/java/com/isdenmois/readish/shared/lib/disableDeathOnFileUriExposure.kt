package com.isdenmois.readish.shared.lib

import android.os.StrictMode
import java.lang.Exception
import java.lang.reflect.Method

fun disableDeathOnFileUriExposure() {
    try {
        val m: Method = StrictMode::class.java.getMethod("disableDeathOnFileUriExposure")
        m.invoke(null)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
