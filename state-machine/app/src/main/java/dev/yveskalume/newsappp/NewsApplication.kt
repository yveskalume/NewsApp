package dev.yveskalume.newsappp

import android.app.Application
import dev.yveskalume.newsappp.data.di.dataSourceModule
import dev.yveskalume.newsappp.data.di.ktorModule
import dev.yveskalume.newsappp.data.di.repositoryModule
import dev.yveskalume.newsappp.ui.screens.home.di.HomeDiModule
import dev.yveskalume.newsappp.ui.screens.search.di.SearchDiModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class NewsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            androidLogger()
            androidContext(this@NewsApplication)
            modules(
                ktorModule,
                dataSourceModule,
                repositoryModule,
                HomeDiModule,
                SearchDiModule
            )
        }
    }
}
