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
package com.example.valguard.app.onboarding.presentation

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
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.valguard.app.onboarding.presentation.components.OnboardingButton
import com.example.valguard.app.onboarding.presentation.components.OnboardingProgressBar
import com.example.valguard.app.onboarding.presentation.steps.CoinSelectionStep
import com.example.valguard.app.onboarding.presentation.steps.FeaturesStep
import com.example.valguard.app.onboarding.presentation.steps.NotificationsStep
import com.example.valguard.app.onboarding.presentation.steps.WelcomeStep
import com.example.valguard.theme.AppTheme
import com.example.valguard.theme.LocalCryptoColors
import com.example.valguard.theme.LocalCryptoTypography
import com.example.valguard.theme.Slate900
import com.example.valguard.theme.Slate950
import kotlinx.coroutines.launch
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
    
    // Pager state for swipe gestures
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { 4 }
    )
    val coroutineScope = rememberCoroutineScope()
    
    // Set navigation callback
    LaunchedEffect(Unit) {
        viewModel.setNavigationCallback(onComplete)
    }
    
    // Sync pager with ViewModel state - only when ViewModel initiates the change (e.g., button press)
    LaunchedEffect(state.currentStep) {
        // Only animate if the pager is not already at the target and not currently scrolling
        if (pagerState.currentPage != state.currentStep && !pagerState.isScrollInProgress) {
            pagerState.animateScrollToPage(
                page = state.currentStep,
                animationSpec = tween(
                    durationMillis = OnboardingViewModel.TRANSITION_DURATION_MS.toInt(),
                    easing = androidx.compose.animation.core.FastOutSlowInEasing
                )
            )
        }
    }
    
    // Sync ViewModel with pager swipes (only when user finishes swiping)
    // This updates the ViewModel to match where the pager settled
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }.collect { settledPage ->
            // Only sync when page is different from ViewModel state and not transitioning
            if (settledPage != state.currentStep && !state.isTransitioning) {
                // User swiped to a different page - directly sync ViewModel
                viewModel.syncToStep(settledPage)
            }
        }
    }
    
    
    val stepGradient = Brush.horizontalGradient(stepColors)
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Static gradient background - clean, professional, no motion
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Slate950, // slate-950 - deep top
                            Slate900, // slate-900 - lighter middle
                            Color(0xFF1E293B), // slate-800 - subtle lift
                            Slate900  // slate-900 - return to depth
                        )
                    )
                )
        )
        
        // Main content - no card container, content floats freely
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            Spacer(modifier = Modifier.height(dimensions.verticalSpacing))
            
            // Top navigation bar - dots + skip only (clean)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimensions.screenPadding)
                    .semantics(mergeDescendants = false) {
                        // Ensure navigation elements are separately discoverable
                    },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Empty space for balance
                Spacer(modifier = Modifier.width(60.dp))
                
                // Progress dots - center
                SimpleDotProgress(
                    currentStep = pagerState.currentPage,
                    totalSteps = 4,
                    activeColor = stepColors.first(),
                    modifier = Modifier.semantics {
                        contentDescription = "Step ${pagerState.currentPage + 1} of 4"
                    }
                )
                
                // Skip button - top right (only on steps 0-2)
                if (pagerState.currentPage < 3) {
                    TextButton(
                        onClick = { viewModel.onEvent(OnboardingEvent.SkipToEnd) },
                        modifier = Modifier.semantics {
                            contentDescription = "Skip onboarding and go to final step"
                        }
                    ) {
                        Text(
                            text = "Skip",
                            style = typography.titleMedium,
                            color = colors.textTertiary.copy(alpha = 0.7f)
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(60.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(dimensions.verticalSpacing * 2))
            
            // Swipeable content area with HorizontalPager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                userScrollEnabled = true // Enable swipe gestures
            ) { page ->
                // Responsive max-width container for tablets
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .then(
                                // Limit content width on tablets to prevent over-wide layouts
                                if (dimensions.screenPadding > 24.dp) {
                                    Modifier.padding(horizontal = dimensions.screenPadding)
                                } else {
                                    Modifier
                                }
                            )
                            .verticalScroll(rememberScrollState())
                    ) {
                        when (page) {
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
                        
                        Spacer(modifier = Modifier.height(dimensions.verticalSpacing * 2))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(dimensions.verticalSpacing))

            // Floating Continue button with glass-morphism
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimensions.screenPadding * 2)
            ) {
                OnboardingButton(
                    currentStep = state.currentStep,
                    enabled = canProceed && !state.isTransitioning,
                    gradient = stepGradient,
                    onClick = { viewModel.onEvent(OnboardingEvent.NextStep) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            Spacer(modifier = Modifier.height(dimensions.verticalSpacing * 2))
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
 * Success overlay - quiet confidence.
 *
 * Displays after completing onboarding with minimal celebration.
 * Calm, confident, professional.
 */
@Composable
private fun SuccessAnimationOverlay() {
    val colors = LocalCryptoColors.current
    val typography = LocalCryptoTypography.current
    val dimensions = AppTheme.dimensions
    
    // Subtle fade in - no scale animation
    val alpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(400)
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(alpha)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        colors.profit.copy(alpha = 0.6f),
                        colors.profit.copy(alpha = 0.4f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Simple checkmark - no animation
            Box(
                modifier = Modifier
                    .size(dimensions.appIconSize)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Success",
                    tint = colors.profit,
                    modifier = Modifier.size(dimensions.appIconSize * 0.5f)
                )
            }
            
            Spacer(modifier = Modifier.height(dimensions.verticalSpacing * 2))
            
            // Calm text
            Text(
                text = "You're set",
                style = typography.displayMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(dimensions.smallSpacing))
            
            Text(
                text = "Welcome to Valguard",
                style = typography.bodyLarge,
                color = Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Simple dot progress indicator.
 *
 * Displays a row of dots representing each step, with the current step highlighted.
 * Premium design with smooth animations and gradient effects.
 *
 * @param currentStep The current step index (0-based)
 * @param totalSteps Total number of steps
 * @param activeColor Color for the active step dot
 * @param inactiveColor Color for inactive step dots
 * @param modifier Optional modifier for the component
 */
@Composable
private fun SimpleDotProgress(
    currentStep: Int,
    totalSteps: Int,
    activeColor: Color,
    inactiveColor: Color = Color.White.copy(alpha = 0.25f),
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(totalSteps) { index ->
            val isActive = index == currentStep
            val isPassed = index < currentStep
            
            // Subtle width animation - whisper progress
            val width by animateFloatAsState(
                targetValue = if (isActive) 20f else 8f,
                animationSpec = tween(300)
            )
            
            Box(
                modifier = Modifier
                    .width(width.dp)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        when {
                            isActive -> activeColor
                            isPassed -> activeColor.copy(alpha = 0.4f)
                            else -> inactiveColor
                        }
                    )
            )
        }
    }
}




