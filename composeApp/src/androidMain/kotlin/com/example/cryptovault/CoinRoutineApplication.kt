package com.example.cryptovault

import android.app.Application
import com.example.cryptovault.app.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class CoinRoutineApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidLogger()
            androidContext(this@CoinRoutineApplication)
        }
    }
}