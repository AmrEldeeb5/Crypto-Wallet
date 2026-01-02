package com.example.cryptovault

import android.app.Application
import com.example.cryptovault.app.di.initKoin
import com.example.cryptovault.app.splash.data.initializeDeviceCapabilityDetector
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class CoinRoutineApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        
        // Initialize device capability detector context before Koin
        initializeDeviceCapabilityDetector(this)
        
        initKoin {
            androidLogger()
            androidContext(this@CoinRoutineApplication)
        }
    }
}