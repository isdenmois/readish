package com.isdenmois.readish.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

import com.isdenmois.readish.shared.lib.disableDeathOnFileUriExposure


@HiltAndroidApp
class ReadishApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        disableDeathOnFileUriExposure()
    }
}
