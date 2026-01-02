/**
 * OnboardingScreen.kt
 *
 * Main screen composable for the onboarding flow. Orchestrates the
 * multi-step onboarding experience with animated transitions between
 * steps, progress tracking, and completion handling.
 *
 * Features:
 * - Animated background with floating crypto symbols
 * - Step-by-step navigation with progress bar
 * - Animated content transitions between steps
 * - Skip confirmation dialog
 * - Success animation on completion
 *
 * @see OnboardingViewModel for state management
 * @see OnboardingState for the state model
 * @see WelcomeStep, FeaturesStep, CoinSelectionStep, NotificationsStep for step content
 */
package com.example.cryptovault.app.onboarding.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.cryptovault.app.onboarding.presentation.components.OnboardingBackground
import com.example.cryptovault.app.onboarding.presentation.components.OnboardingButton
import com.example.cryptovault.app.onboarding.presentation.components.OnboardingProgressBar
import com.example.cryptovault.app.onboarding.presentation.steps.CoinSelectionStep
import com.example.cryptovault.app.onboarding.presentation.steps.FeaturesStep
import com.example.cryptovault.app.onboarding.presentation.steps.NotificationsStep
import com.example.cryptovault.app.onboarding.presentation.steps.WelcomeStep
import com.example.cryptovault.theme.AppTheme
import com.example.cryptovault.theme.LocalCryptoColors
import com.example.cryptovault.theme.LocalCryptoTypography
import org.koin.compose.viewmodel.koinViewModel

/**
 * Main onboarding screen composable.
 *
 * Displays the complete onboarding flow with animated transitions,
 * progress tracking, and navigation controls. Manages the visual
 * presentation while delegating state management to [OnboardingViewModel].
 *
 * @param onComplete Callback invoked when onboarding is completed
 * @param viewModel The ViewModel managing onboarding state (injected via Koin)
 */
@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val canProceed by viewModel.canProceed.collectAsStateWithLifecycle()
    val stepColors by viewModel.currentStepColors.collectAsStateWithLifecycle()
    
    val colors = LocalCryptoColors.current
    val typography = LocalCryptoTypography.current
    val dimensions = AppTheme.dimensions
    
    // Set navigation callback
    LaunchedEffect(Unit) {
        viewModel.setNavigationCallback(onComplete)
    }
    
    val stepGradient = Brush.horizontalGradient(stepColors)
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Animated background
        OnboardingBackground()
        
        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            Spacer(modifier = Modifier.height(dimensions.smallSpacing))
            
            // Progress bar at the very top
            OnboardingProgressBar(
                currentStep = state.currentStep,
                stepGradient = stepGradient
            )
            
            Spacer(modifier = Modifier.height(dimensions.verticalSpacing))
            
            // Main content card - React: bg-slate-900/50 - more transparent/darker
            val slateCardBackground = Color(0xFF0F172A).copy(alpha = 0.35f) // slate-900 with more transparency
            val slateBorder = Color(0xFF334155).copy(alpha = 0.5f) // slate-700/50
            val cardShape = RoundedCornerShape(dimensions.cardCornerRadius)
            
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = dimensions.screenPadding)
                    .clip(cardShape)
                     //.background(slateCardBackground)
                    //.border(1.dp, slateBorder, cardShape)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Scrollable content area
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                        ) {
                            // Step content with animated transitions
                            AnimatedContent(
                                targetState = state.currentStep,
                                transitionSpec = {
                                    if (targetState > initialState) {
                                        slideInHorizontally { it } + fadeIn() togetherWith
                                            slideOutHorizontally { -it } + fadeOut()
                                    } else {
                                        slideInHorizontally { -it } + fadeIn() togetherWith
                                            slideOutHorizontally { it } + fadeOut()
                                    }
                                }
                            ) { step ->
                                when (step) {
                                    0 -> WelcomeStep()
                                    1 -> FeaturesStep()
                                    2 -> CoinSelectionStep(
                                        selectedCoins = state.selectedCoins,
                                        onToggleCoin = { viewModel.onEvent(OnboardingEvent.ToggleCoin(it)) }
                                    )
                                    3 -> NotificationsStep(
                                        notificationsEnabled = state.notificationsEnabled,
                                        onToggleNotifications = { viewModel.onEvent(OnboardingEvent.ToggleNotifications) }
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(dimensions.smallSpacing))
                        }
                    }
                    
                    // Divider above button
//                    HorizontalDivider(
//                        color = Color(0xFF334155).copy(alpha = 0.5f),
//                        thickness = 1.dp
//                    )
                    
                    Spacer(modifier = Modifier.height(dimensions.verticalSpacing))

                    // Fixed Continue button at bottom of card
                    OnboardingButton(
                        currentStep = state.currentStep,
                        enabled = canProceed && !state.isTransitioning,
                        gradient = stepGradient,
                        onClick = { viewModel.onEvent(OnboardingEvent.NextStep) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = dimensions.screenPadding)
                    )

                    Spacer(modifier = Modifier.height(dimensions.smallSpacing))

                    // Back button - styled similar to Continue button but with outline style
                    AnimatedVisibility(
                        visible = state.currentStep > 0,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(dimensions.buttonHeight)
                                .padding(horizontal = dimensions.screenPadding)
                                .clip(RoundedCornerShape(dimensions.cardCornerRadius))
                                .background(Color.Transparent)
                                .border(
                                    width = 2.dp,
                                    brush = stepGradient,
                                    shape = RoundedCornerShape(dimensions.cardCornerRadius)
                                )
                                .clickable { viewModel.onEvent(OnboardingEvent.PreviousStep) }
                                .semantics {
                                    role = androidx.compose.ui.semantics.Role.Button
                                    contentDescription = "Back"
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Back",
                                style = typography.labelLarge,
                                color = Color.White
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(dimensions.verticalSpacing))
                }
            }

            // Skip for now button - OUTSIDE the card (only on steps 0-2)
            if (state.currentStep < 3) {
                Spacer(modifier = Modifier.height(dimensions.verticalSpacing))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimensions.screenPadding * 2)
                        .clip(RoundedCornerShape(dimensions.cardCornerRadius))
                        .background(colors.textSecondary.copy(alpha = 0.1f))
                        .clickable { viewModel.onEvent(OnboardingEvent.SkipToEnd) }
                        .padding(vertical = dimensions.smallSpacing)
                        .semantics {
                            role = androidx.compose.ui.semantics.Role.Button
                            contentDescription = "Skip onboarding"
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Skip for now",
                        style = typography.bodyMedium,
                        color = colors.textSecondary.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(dimensions.screenPadding))
            }

        }
        
        // Skip confirmation dialog
        if (state.showSkipDialog) {
            SkipConfirmationDialog(
                onConfirm = { viewModel.onEvent(OnboardingEvent.ConfirmSkip) },
                onDismiss = { viewModel.onEvent(OnboardingEvent.DismissSkipDialog) }
            )
        }
        
        // Success animation overlay
        if (state.showSuccessAnimation) {
            SuccessAnimationOverlay()
        }
    }
}

/**
 * Dialog for confirming skip action.
 *
 * Displays when user taps "Skip for now", asking for confirmation
 * before jumping to the end of onboarding.
 *
 * @param onConfirm Callback when user confirms skipping
 * @param onDismiss Callback when user cancels or dismisses dialog
 */
@Composable
private fun SkipConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val colors = LocalCryptoColors.current
    val typography = LocalCryptoTypography.current
    val dimensions = AppTheme.dimensions
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Skip onboarding?",
                style = typography.titleMedium,
                color = colors.textPrimary
            )
        },
        text = {
            Text(
                text = "You can always customize your settings later in the app.",
                style = typography.bodyMedium,
                color = colors.textSecondary
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = "Skip",
                    color = colors.accentBlue500
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Continue setup",
                    color = colors.textSecondary
                )
            }
        },
        containerColor = colors.cardBackground
    )
}

/**
 * Full-screen success animation overlay.
 *
 * Displays after completing onboarding with an animated checkmark
 * and welcome message before navigating to the main app.
 */
@Composable
private fun SuccessAnimationOverlay() {
    val colors = LocalCryptoColors.current
    val typography = LocalCryptoTypography.current
    val dimensions = AppTheme.dimensions
    
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(500)
    )
    
    val alpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(300)
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(alpha)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        colors.profit.copy(alpha = 0.9f),
                        colors.profit.copy(alpha = 0.7f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Animated checkmark
            Box(
                modifier = Modifier
                    .size(dimensions.appIconSize + 36.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Success",
                    tint = colors.profit,
                    modifier = Modifier.size(dimensions.appIconSize * 0.6f)
                )
            }
            
            Spacer(modifier = Modifier.height(dimensions.verticalSpacing))
            
            // Welcome text
            Text(
                text = "Welcome aboard!",
                style = typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(dimensions.smallSpacing))
            
            Text(
                text = "Your crypto journey begins now",
                style = typography.bodyLarge,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )
        }
    }
}




