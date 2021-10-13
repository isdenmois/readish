package com.isdenmois.readish.app

import android.app.Application

import com.isdenmois.readish.shared.lib.disableDeathOnFileUriExposure
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class ReadishApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        disableDeathOnFileUriExposure()
        startKoin {
            androidLogger()
            androidContext(applicationContext)
            modules(appModule)
        }
    }
}
