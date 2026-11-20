package com.isdenmois.readish

import android.content.Context
import android.content.SharedPreferences
import com.isdenmois.readish.ui.AppLauncher
import com.isdenmois.readish.ui.ReaderLauncher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class MainModule {
    @Singleton
    @Provides
    fun provideReaderLauncher(@ApplicationContext context: Context) = ReaderLauncher(context)

    @Singleton
    @Provides
    fun provideAppLauncher(@ApplicationContext context: Context) = AppLauncher(context)

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        val file = File(context.cacheDir, "sp.txt")

        return context.getSharedPreferences(file.name, Context.MODE_PRIVATE)
    }
}
