package com.example.cryptovault.app.di

import com.example.cryptovault.app.coindetail.presentation.CoinDetailViewModel
import com.example.cryptovault.app.coins.data.remote.impl.KtorCoinsRemoteDataSource
import com.example.cryptovault.app.coins.domain.usecase.GetCoinDetailsUseCase
import com.example.cryptovault.app.coins.domain.usecase.GetCoinPriceHistoryUseCase
import com.example.cryptovault.app.coins.domain.usecase.GetCoinsListUseCase
import com.example.cryptovault.app.coins.domain.api.CoinsRemoteDataSource
import com.example.cryptovault.app.coins.presentation.CoinsListViewModel
import com.example.cryptovault.app.compare.data.ComparisonRepository
import com.example.cryptovault.app.compare.presentation.CompareViewModel
import com.example.cryptovault.app.core.database.portfolio.PortfolioDatabase
import com.example.cryptovault.app.core.database.portfolio.getPortfolioDatabase
import com.example.cryptovault.app.core.network.HttpClientFactory
import com.example.cryptovault.app.dca.data.DCARepository
import com.example.cryptovault.app.dca.presentation.DCAViewModel
import com.example.cryptovault.app.leaderboard.presentation.LeaderboardViewModel
import com.example.cryptovault.app.onboarding.data.OnboardingRepository
import com.example.cryptovault.app.onboarding.presentation.OnboardingViewModel
import com.example.cryptovault.app.portfolio.data.PortfolioRepositoryImpl
import com.example.cryptovault.app.portfolio.domain.PortfolioRepository
import com.example.cryptovault.app.portfolio.presentation.PortfolioViewModel
import com.example.cryptovault.app.realtime.data.ExponentialBackoffStrategy
import com.example.cryptovault.app.realtime.data.FallbackPoller
import com.example.cryptovault.app.realtime.data.CoinCapWebSocketClient
import com.example.cryptovault.app.realtime.data.PriceRepositoryImpl
import com.example.cryptovault.app.realtime.data.SubscriptionManagerImpl
import com.example.cryptovault.app.realtime.domain.ObservePriceUpdatesUseCase
import com.example.cryptovault.app.realtime.domain.PriceRepository
import com.example.cryptovault.app.realtime.domain.ReconnectionStrategy
import com.example.cryptovault.app.realtime.domain.SubscriptionManager
import com.example.cryptovault.app.realtime.domain.WebSocketClient
import com.example.cryptovault.app.trade.domain.BuyCoinUseCase
import com.example.cryptovault.app.trade.domain.SellCoinUseCase
import com.example.cryptovault.app.trade.presentation.buy.BuyViewModel
import com.example.cryptovault.app.trade.presentation.sell.SellViewModel
import com.example.cryptovault.app.watchlist.data.WatchlistRepositoryImpl
import com.example.cryptovault.app.watchlist.domain.WatchlistRepository
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
    viewModel { CoinsListViewModel(get(), get(), get()) }
    viewModel { PortfolioViewModel(get(), get()) }
    viewModel { (coinId: String) -> BuyViewModel(coinId, get(), get(), get(), get()) }
    viewModel { (coinId: String) -> SellViewModel(coinId, get(), get(), get(), get()) }
    viewModel { CoinDetailViewModel(get(), get(), get()) }
    viewModel { DCAViewModel(get()) }
    viewModel { CompareViewModel(get()) }
    viewModel { LeaderboardViewModel() }
    viewModel { OnboardingViewModel(get()) }
}
