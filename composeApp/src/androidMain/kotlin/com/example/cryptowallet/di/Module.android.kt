package com.example.cryptowallet.app.di

import androidx.room.RoomDatabase
import com.example.cryptowallet.app.core.database.portfolio.PortfolioDatabase
import com.example.cryptowallet.app.core.database.getPortfolioDatabaseBuilder
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformModule = module {

    // core
    single<HttpClientEngine> { OkHttp.create() }
    singleOf(::getPortfolioDatabaseBuilder).bind<RoomDatabase.Builder<PortfolioDatabase>>()
}