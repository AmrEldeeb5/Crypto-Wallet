package com.example.cryptowallet.app.di

import com.example.cryptowallet.app.coins.data.remote.impl.KtorCoinsRemoteDataSource
import com.example.cryptowallet.app.coins.domain.GetCoinsListUseCase
import com.example.cryptowallet.app.coins.domain.api.CoinsRemoteDataSource
import com.example.cryptowallet.app.coins.presentation.CoinsListViewModel
import com.example.cryptowallet.app.core.network.HttpClientFactory
import io.ktor.client.HttpClient
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

typealias KoinAppDeclaration = org.koin.core.KoinApplication.() -> Unit

fun initKoin(config: KoinAppDeclaration? = null) =
    startKoin {
        config?.invoke(this)
        modules(
            sharedModule,
            platformModule,
        )
    }

expect val platformModule: Module

val sharedModule = module {

    // core
    single<HttpClient> { HttpClientFactory.create(get()) }

    // data sources
    single<CoinsRemoteDataSource> { KtorCoinsRemoteDataSource(get()) }

    // use cases
    single { GetCoinsListUseCase(get()) }

    // view models
    single { CoinsListViewModel(get()) }
}