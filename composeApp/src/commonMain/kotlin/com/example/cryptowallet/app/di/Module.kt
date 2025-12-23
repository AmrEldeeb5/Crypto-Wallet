package com.example.cryptowallet.app.di

import com.example.cryptowallet.app.coins.data.remote.impl.KtorCoinsRemoteDataSource
import com.example.cryptowallet.app.coins.domain.usecase.GetCoinDetailsUseCase
import com.example.cryptowallet.app.coins.domain.usecase.GetCoinPriceHistoryUseCase
import com.example.cryptowallet.app.coins.domain.usecase.GetCoinsListUseCase
import com.example.cryptowallet.app.coins.domain.api.CoinsRemoteDataSource
import com.example.cryptowallet.app.coins.presentation.CoinsListViewModel
import com.example.cryptowallet.app.core.database.portfolio.PortfolioDatabase
import com.example.cryptowallet.app.core.database.portfolio.getPortfolioDatabase
import com.example.cryptowallet.app.core.network.HttpClientFactory
import com.example.cryptowallet.app.portfolio.data.PortfolioRepositoryImpl
import com.example.cryptowallet.app.portfolio.domain.PortfolioRepository
import io.ktor.client.HttpClient
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
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

    // portfolio
    single { getPortfolioDatabase(get()) }
    single { get<PortfolioDatabase>().portfolioDao() }
    singleOf(::PortfolioRepositoryImpl).bind<PortfolioRepository>()

    // use cases
    single { GetCoinsListUseCase(get()) }
    single { GetCoinPriceHistoryUseCase(get()) }
    single { GetCoinDetailsUseCase(get()) }

    // view models
    //viewModelOf(::CoinsListViewModel)
    viewModel { CoinsListViewModel(get(), get())  }
}