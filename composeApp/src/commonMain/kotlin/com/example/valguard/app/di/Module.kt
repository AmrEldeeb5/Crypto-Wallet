package com.example.valguard.app.di

import com.example.valguard.app.coindetail.presentation.CoinDetailViewModel
import com.example.valguard.app.coins.data.remote.impl.KtorCoinsRemoteDataSource
import com.example.valguard.app.coins.domain.usecase.GetCoinDetailsUseCase
import com.example.valguard.app.coins.domain.usecase.GetCoinPriceHistoryUseCase
import com.example.valguard.app.coins.domain.usecase.GetCoinsListUseCase
import com.example.valguard.app.coins.domain.api.CoinsRemoteDataSource
import com.example.valguard.app.coins.presentation.CoinsListViewModel
import com.example.valguard.app.compare.data.ComparisonRepository
import com.example.valguard.app.compare.presentation.CompareViewModel
import com.example.valguard.app.core.database.portfolio.PortfolioDatabase
import com.example.valguard.app.core.database.portfolio.getPortfolioDatabase
import com.example.valguard.app.core.network.HttpClientFactory
import com.example.valguard.app.dca.data.DCARepository
import com.example.valguard.app.dca.presentation.DCAViewModel
import com.example.valguard.app.onboarding.data.OnboardingRepository
import com.example.valguard.app.onboarding.presentation.OnboardingViewModel
import com.example.valguard.app.portfolio.data.PortfolioRepositoryImpl
import com.example.valguard.app.portfolio.domain.PortfolioRepository
import com.example.valguard.app.portfolio.presentation.PortfolioViewModel
import com.example.valguard.app.realtime.data.ExponentialBackoffStrategy
import com.example.valguard.app.realtime.data.FallbackPoller
import com.example.valguard.app.realtime.data.CoinCapWebSocketClient
import com.example.valguard.app.realtime.data.PriceRepositoryImpl
import com.example.valguard.app.realtime.data.SubscriptionManagerImpl
import com.example.valguard.app.realtime.domain.ObservePriceUpdatesUseCase
import com.example.valguard.app.realtime.domain.PriceRepository
import com.example.valguard.app.realtime.domain.ReconnectionStrategy
import com.example.valguard.app.realtime.domain.SubscriptionManager
import com.example.valguard.app.realtime.domain.WebSocketClient
import com.example.valguard.app.splash.data.*
import com.example.valguard.app.splash.domain.DeviceCapabilityDetector
import com.example.valguard.app.splash.domain.InitializationOrchestrator
import com.example.valguard.app.splash.presentation.SplashViewModel
import com.example.valguard.app.trade.domain.BuyCoinUseCase
import com.example.valguard.app.trade.domain.SellCoinUseCase
import com.example.valguard.app.trade.presentation.buy.BuyViewModel
import com.example.valguard.app.trade.presentation.sell.SellViewModel
import com.example.valguard.app.watchlist.data.WatchlistRepositoryImpl
import com.example.valguard.app.watchlist.domain.WatchlistRepository
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

    // splash screen initialization
    single<DeviceCapabilityDetector> { createDeviceCapabilityDetector() }
    single<SecureStorageInitializer> { RealSecureStorageInitializer() }
    single<DatabaseInitializer> { RealDatabaseInitializer() }
    single<NetworkWarmer> { RealNetworkWarmer() }
    single<ConfigLoader> { RealConfigLoader() }
    single<UIReadinessChecker> { RealUIReadinessChecker() }
    single<InitializationOrchestrator> { 
        RealInitializationOrchestrator(
            get(), get(), get(), get(), get()
        )
    }

    // data sources
    single<CoinsRemoteDataSource> { KtorCoinsRemoteDataSource(get()) }

    // portfolio
    single { getPortfolioDatabase(get()) }
    single { get<PortfolioDatabase>().portfolioDao() }
    single { get<PortfolioDatabase>().UserBalanceDao() }
    singleOf(::PortfolioRepositoryImpl).bind<PortfolioRepository>()

    // watchlist
    single { get<PortfolioDatabase>().watchlistDao() }
    singleOf(::WatchlistRepositoryImpl).bind<WatchlistRepository>()
    
    // onboarding
    single { OnboardingRepository(get()) }
    
    // DCA
    single { get<PortfolioDatabase>().dcaScheduleDao() }
    single { get<PortfolioDatabase>().dcaExecutionDao() }
    single { DCARepository(get(), get()) }
    
    // Comparison
    single { get<PortfolioDatabase>().savedComparisonDao() }
    single { ComparisonRepository(get()) }

    // real-time price updates (CoinCap WebSocket API)
    single<ReconnectionStrategy> { ExponentialBackoffStrategy() }
    single<SubscriptionManager> { SubscriptionManagerImpl() }
    single<WebSocketClient> { CoinCapWebSocketClient(get(), get()) }
    single { FallbackPoller(get()) }
    single<PriceRepository> { PriceRepositoryImpl(get(), get()) }
    single { ObservePriceUpdatesUseCase(get(), get()) }

    // use cases
    single { GetCoinsListUseCase(get()) }
    single { GetCoinPriceHistoryUseCase(get()) }
    single { GetCoinDetailsUseCase(get()) }
    single { BuyCoinUseCase(get()) }
    single { SellCoinUseCase(get()) }

    // view models
    viewModel { SplashViewModel(get(), get()) }
    viewModel { CoinsListViewModel(get(), get(), get()) }
    viewModel { PortfolioViewModel(get(), get()) }
    viewModel { (coinId: String) -> BuyViewModel(coinId, get(), get(), get(), get()) }
    viewModel { (coinId: String) -> SellViewModel(coinId, get(), get(), get(), get()) }
    viewModel { CoinDetailViewModel(get(), get(), get()) }
    viewModel { DCAViewModel(get()) }
    viewModel { CompareViewModel(get()) }
    viewModel { OnboardingViewModel(get()) }
}
