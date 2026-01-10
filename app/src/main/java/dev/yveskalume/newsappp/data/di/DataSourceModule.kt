package dev.yveskalume.newsappp.data.di

import dev.yveskalume.newsappp.data.network.datasource.NewsDataSource
import dev.yveskalume.newsappp.data.network.datasource.NewsDataSourceImpl
import dev.yveskalume.newsappp.data.network.datasource.SourcesDataSource
import dev.yveskalume.newsappp.data.network.datasource.SourcesDataSourceImpl
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val dataSourceModule = module {
    singleOf(::NewsDataSourceImpl) bind NewsDataSource::class
    singleOf(::SourcesDataSourceImpl) bind SourcesDataSource::class
}
