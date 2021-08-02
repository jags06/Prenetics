package com.example.preneticstest

import android.app.Application
import android.content.Context
import com.example.preneticstest.base.viewmodelmodule.viewModelModule
import com.example.preneticstest.db.module.realmModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level.NONE
import timber.log.Timber

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        initKoin(applicationContext)

    }

    private fun initKoin(context: Context) {
        val koinModule = listOf(realmModule, viewModelModule)
        startKoin {
            androidLogger(NONE)
            androidContext(context)
            modules(koinModule)

        }
    }
}