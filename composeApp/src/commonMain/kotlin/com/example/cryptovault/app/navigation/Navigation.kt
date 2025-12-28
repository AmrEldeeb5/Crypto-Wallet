package com.example.cryptovault.app.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.cryptovault.app.coindetail.presentation.CoinDetailScreen
import com.example.cryptovault.app.coins.presentation.CoinsListScreen
import com.example.cryptovault.app.compare.presentation.CompareScreen
import com.example.cryptovault.app.dca.presentation.DCAScreen
import com.example.cryptovault.app.leaderboard.presentation.LeaderboardScreen
import com.example.cryptovault.app.main.presentation.MainScreen
import com.example.cryptovault.app.onboarding.data.OnboardingRepository
import com.example.cryptovault.app.onboarding.presentation.OnboardingScreen
import com.example.cryptovault.app.portfolio.presentation.PortfolioScreen
import com.example.cryptovault.app.referral.presentation.ReferralScreen
import com.example.cryptovault.app.splash.SplashScreen
import com.example.cryptovault.app.trade.presentation.buy.BuyScreen
import com.example.cryptovault.app.trade.presentation.sell.SellScreen
import org.koin.compose.koinInject

private const val ANIMATION_DURATION = 300

@Composable
fun Navigation(
    navController: NavHostController = rememberNavController()
) {
    val onboardingRepository: OnboardingRepository = koinInject()
    var nextDestination: Screens by remember { mutableStateOf(Screens.Main) }
    var isLoading by remember { mutableStateOf(true) }
    
    // Check if onboarding is completed
    LaunchedEffect(Unit) {
        val isCompleted = onboardingRepository.isOnboardingCompleted()
        nextDestination = if (isCompleted) Screens.Main else Screens.Onboarding
        isLoading = false
    }
    
    if (isLoading) {
        // Show nothing while loading
        return
    }
    
    NavHost(
        navController = navController,
        startDestination = Screens.Splash
    ) {
        // Splash Screen with fade transition
        composable<Screens.Splash>(
            enterTransition = { fadeIn(animationSpec = tween(ANIMATION_DURATION)) },
            exitTransition = { fadeOut(animationSpec = tween(ANIMATION_DURATION)) }
        ) {
            SplashScreen(
                onSplashComplete = {
                    navController.navigate(nextDestination) {
                        // Remove splash from back stack
                        popUpTo(Screens.Splash) { inclusive = true }
                    }
                }
            )
        }
        
        // Onboarding Screen with fade transition
        composable<Screens.Onboarding>(
            enterTransition = { fadeIn(animationSpec = tween(ANIMATION_DURATION)) },
            exitTransition = { fadeOut(animationSpec = tween(ANIMATION_DURATION)) }
        ) {
            OnboardingScreen(
                onComplete = {
                    navController.navigate(Screens.Main) {
                        popUpTo(Screens.Onboarding) { inclusive = true }
                    }
                }
            )
        }
        
        composable<Screens.Main> {
            MainScreen(
                onBuyClick = { coinId ->
                    navController.navigate(Screens.Buy(coinId))
                },
                onSellClick = { coinId ->
                    navController.navigate(Screens.Sell(coinId))
                },
                onCoinClick = { coinId ->
                    navController.navigate(Screens.CoinDetail(coinId))
                },
                onDCAClick = {
                    navController.navigate(Screens.DCA)
                },
                onCompareClick = {
                    navController.navigate(Screens.Compare)
                },
                onLeaderboardClick = {
                    navController.navigate(Screens.Leaderboard)
                },
                onReferralClick = {
                    navController.navigate(Screens.Referral)
                }
            )
        }
        composable<Screens.Portfolio> {
            PortfolioScreen(
                onCoinItemClicked = { coinId ->
                    navController.navigate(Screens.Sell(coinId))
                },
                onDiscoverCoinsClicked = {
                    navController.navigate(Screens.Coins)
                }
            )
        }
        composable<Screens.Coins> {
            CoinsListScreen(
                onCoinClicked = { coinId ->
                    navController.navigate(Screens.Buy(coinId))
                }
            )
        }
        composable<Screens.Sell> { backStackEntry ->
            val sell: Screens.Sell = backStackEntry.toRoute()
            SellScreen(
                coinId = sell.coinId,
                navigateToPortfolio = {
                    navController.navigate(Screens.Main) {
                        popUpTo(Screens.Main) { inclusive = true }
                    }
                }
            )
        }
        composable<Screens.Buy> { backStackEntry ->
            val buy: Screens.Buy = backStackEntry.toRoute()
            BuyScreen(
                coinId = buy.coinId,
                navigateToPortfolio = {
                    navController.navigate(Screens.Main) {
                        popUpTo(Screens.Main) { inclusive = true }
                    }
                }
            )
        }
        
        // Coin Detail Screen with slide-up animation
        composable<Screens.CoinDetail>(
            enterTransition = {
                slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(ANIMATION_DURATION)
                ) + fadeIn(animationSpec = tween(ANIMATION_DURATION))
            },
            exitTransition = {
                slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(ANIMATION_DURATION)
                ) + fadeOut(animationSpec = tween(ANIMATION_DURATION))
            }
        ) { backStackEntry ->
            val coinDetail: Screens.CoinDetail = backStackEntry.toRoute()
            CoinDetailScreen(
                coinId = coinDetail.coinId,
                onDismiss = { navController.popBackStack() },
                onBuyClick = { coinId ->
                    navController.navigate(Screens.Buy(coinId))
                },
                onSellClick = { coinId ->
                    navController.navigate(Screens.Sell(coinId))
                }
            )
        }
        
        // DCA Screen with slide transition
        composable<Screens.DCA>(
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(ANIMATION_DURATION)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(ANIMATION_DURATION)
                )
            }
        ) {
            DCAScreen(
                onBack = { navController.popBackStack() },
                onNavigateToBuy = { coinId ->
                    navController.navigate(Screens.Buy(coinId))
                }
            )
        }
        
        // Compare Screen with slide transition
        composable<Screens.Compare>(
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(ANIMATION_DURATION)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(ANIMATION_DURATION)
                )
            }
        ) {
            CompareScreen(
                onBack = { navController.popBackStack() }
            )
        }
        
        // Referral Screen with slide transition
        composable<Screens.Referral>(
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(ANIMATION_DURATION)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(ANIMATION_DURATION)
                )
            }
        ) {
            ReferralScreen(
                onBack = { navController.popBackStack() }
            )
        }
        
        // Leaderboard Screen with slide transition
        composable<Screens.Leaderboard>(
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(ANIMATION_DURATION)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(ANIMATION_DURATION)
                )
            }
        ) {
            LeaderboardScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
