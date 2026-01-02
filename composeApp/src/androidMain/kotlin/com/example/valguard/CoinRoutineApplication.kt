package com.example.valguard

import android.app.Application
import com.example.valguard.app.di.initKoin
import com.example.valguard.app.splash.data.initializeDeviceCapabilityDetector
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