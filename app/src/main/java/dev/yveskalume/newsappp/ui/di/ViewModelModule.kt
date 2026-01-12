package dev.yveskalume.newsappp.ui.di

import dev.yveskalume.newsappp.ui.screens.home.HomeViewModel
import dev.yveskalume.newsappp.ui.screens.search.SearchViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { HomeViewModel(get(), get()) }
    viewModel { SearchViewModel(get()) }
}
