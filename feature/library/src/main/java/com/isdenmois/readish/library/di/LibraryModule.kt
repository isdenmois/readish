package com.isdenmois.readish.library.di

import android.content.Context
import com.isdenmois.readish.ui.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LibraryModule {
    @Singleton
    @Provides
    @BooksDir
    fun provideBooksDir(@ApplicationContext context: Context): File = File(context.getString(R.string.books_dir))
}
