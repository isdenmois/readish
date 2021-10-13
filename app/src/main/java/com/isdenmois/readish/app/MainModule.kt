package com.isdenmois.readish.app

import com.isdenmois.readish.screens.home.HomeViewModel
import com.isdenmois.readish.screens.transfers.TransfersViewModel
import com.isdenmois.readish.shared.api.alreader.BookRepository
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { BookRepository(get()) }
    viewModel { HomeViewModel(get(), get()) }
    viewModel { TransfersViewModel(get()) }
}
