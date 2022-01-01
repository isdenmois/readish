package com.isdenmois.readish

import android.app.Application
import android.os.StrictMode
import dagger.hilt.android.HiltAndroidApp
import java.lang.Exception
import java.lang.reflect.Method

@HiltAndroidApp
class ReadishApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        disableDeathOnFileUriExposure()
    }

    private fun disableDeathOnFileUriExposure() {
        try {
            val m: Method = StrictMode::class.java.getMethod("disableDeathOnFileUriExposure")
            m.invoke(null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
