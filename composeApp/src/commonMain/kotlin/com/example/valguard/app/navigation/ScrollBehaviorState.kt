package com.example.valguard.app.navigation

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity

/**
 * State for managing smooth hide-on-scroll behavior for app bars and bottom navigation.
 * This provides a smooth, animated experience when scrolling content.
 */
@Stable
class ScrollBehaviorState(
    private val heightOffsetLimit: Float = -Float.MAX_VALUE,
    private val animationSpec: AnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium
    )
) {
    /**
     * The current scroll offset. This value changes as the user scrolls.
     */
    var scrollOffset by mutableFloatStateOf(0f)
        private set

    /**
     * Whether the bars should be visible. Controlled by scroll direction and position.
     */
    var isVisible by mutableStateOf(true)
        private set

    private var lastScrollPosition = 0f
    private var scrollThreshold = 30f // Reduced threshold for more responsive hiding (was 50f)

    /**
     * Creates a NestedScrollConnection to track scroll events and update visibility.
     */
    val nestedScrollConnection = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            val delta = available.y
            val newOffset = (scrollOffset + delta).coerceAtLeast(heightOffsetLimit)
            scrollOffset = newOffset

            // Determine scroll direction and update visibility
            if (delta < -3f) { // More sensitive threshold (was -5f)
                // Scrolling down - hide bars
                if (kotlin.math.abs(scrollOffset - lastScrollPosition) > scrollThreshold) {
                    isVisible = false
                    lastScrollPosition = scrollOffset
                }
            } else if (delta > 3f) { // More sensitive threshold (was 5f)
                // Scrolling up - show bars
                if (kotlin.math.abs(scrollOffset - lastScrollPosition) > scrollThreshold / 2) {
                    isVisible = true
                    lastScrollPosition = scrollOffset
                }
            }

            return Offset.Zero
        }

        override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
            // Show bars after fling ends if at top
            if (scrollOffset >= -10f) {
                isVisible = true
            }
            return super.onPostFling(consumed, available)
        }
    }

    /**
     * Reset the scroll state (useful when switching screens)
     */
    fun reset() {
        scrollOffset = 0f
        lastScrollPosition = 0f
        isVisible = true
    }
}

/**
 * Remember a ScrollBehaviorState instance
 */
@Composable
fun rememberScrollBehaviorState(): ScrollBehaviorState {
    return remember { ScrollBehaviorState() }
}

/**
 * Get animated visibility fraction (0f = hidden, 1f = visible)
 */
@Composable
fun ScrollBehaviorState.animatedVisibilityFraction(): Float {
    val animatedValue by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "BarVisibility"
    )
    return animatedValue
}

