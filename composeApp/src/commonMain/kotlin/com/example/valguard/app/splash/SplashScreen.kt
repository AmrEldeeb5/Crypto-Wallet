/**
 * SplashScreen.kt - V2 Production
 *
 * Production-optimized splash screen with real initialization.
 * Prioritizes trust, performance, and accessibility.
 */
package com.example.valguard.app.splash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.valguard.app.splash.components.GradientText
import com.example.valguard.app.splash.components.OptimizedParticleSystem
import com.example.valguard.app.splash.components.RealProgressBar
import com.example.valguard.app.splash.components.SimplifiedBackground
import com.example.valguard.app.splash.presentation.SplashViewModel
import org.koin.compose.viewmodel.koinViewModel

/**
 * V2 Splash screen with real initialization.
 * 
 * Key improvements:
 * - Real progress from actual initialization tasks
 * - Device capability scaling (particles, animations)
 * - Accessibility support (TalkBack, reduce motion)
 * - Early exit when initialization completes
 * - Faster fade-out (600ms vs 1000ms)
 */
@Composable
fun SplashScreen(
    onSplashComplete: () -> Unit,
    viewModel: SplashViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    var alpha by remember { mutableStateOf(1f) }
    
    // Start initialization
    LaunchedEffect(Unit) {
        viewModel.startInitialization()
    }
    
    // Navigate when complete
    LaunchedEffect(state.isComplete) {
        if (state.isComplete) {
            alpha = 0f
            kotlinx.coroutines.delay(600) // Fade-out duration
            onSplashComplete()
        }
    }
    
    // Release animation resources on disposal
    androidx.compose.runtime.DisposableEffect(Unit) {
        onDispose {
            // Animation resources are automatically released by Compose
            // This ensures cleanup happens when splash is removed
        }
    }
    
    // Fade out animation
    val animatedAlpha by animateFloatAsState(
        targetValue = alpha,
        animationSpec = tween(durationMillis = 600),
        label = "alpha"
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A)) // Slate900 - dark background
            .graphicsLayer { this.alpha = animatedAlpha }
            .semantics { contentDescription = "Loading Valguard" },
        contentAlignment = Alignment.Center
    ) {
        // Simplified background (max 2 orbs)
        SimplifiedBackground(
            modifier = Modifier.fillMaxSize(),
            capabilities = state.deviceCapabilities
        )
        
        // Optimized particles (30-40, or 0 if reduce motion)
        OptimizedParticleSystem(
            modifier = Modifier.fillMaxSize(),
            capabilities = state.deviceCapabilities
        )
        
        // Main content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            
            // App name with gradient
            GradientText(
                text = "Valguard",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.semantics { 
                    contentDescription = "Valguard logo" 
                }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Tagline with gradient
            GradientText(
                text = "Your Premium Crypto Companion",
                style = MaterialTheme.typography.titleLarge
            )
            
            Spacer(modifier = Modifier.height(64.dp)) // Reduced from 80dp - moves progress bar up
            
            // Real progress bar with phase-specific status
            RealProgressBar(
                progress = state.progress,
                phase = state.currentPhase
            )
            
            Spacer(modifier = Modifier.height(80.dp)) // Increased spacing to push footer down
            
            // Version info
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Version 1.0.0",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF475569).copy(alpha = 0.6f) // Reduced opacity
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "© 2025 Valguard. All rights reserved.",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF475569).copy(alpha = 0.6f) // Reduced opacity
                )
            }
        }
    }
}
