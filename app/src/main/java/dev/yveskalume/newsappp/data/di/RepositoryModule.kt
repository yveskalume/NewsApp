package dev.yveskalume.newsappp.data.di

import dev.yveskalume.newsappp.data.repository.NewsRepository
import dev.yveskalume.newsappp.data.repository.NewsRepositoryImpl
import dev.yveskalume.newsappp.data.repository.SourcesRepository
import dev.yveskalume.newsappp.data.repository.SourcesRepositoryImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val repositoryModule = module {
    singleOf(::NewsRepositoryImpl) bind NewsRepository::class
    singleOf(::SourcesRepositoryImpl) bind SourcesRepository::class
}
