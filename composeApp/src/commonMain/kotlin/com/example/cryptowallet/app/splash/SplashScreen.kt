/**
 * SplashScreen.kt
 *
 * Main splash screen for the CryptoVault application.
 * Displays animated branding, loading progress, and feature highlights
 * during the 3-second app initialization sequence.
 *
 * The splash screen features:
 * - Animated background with pulsing orbs
 * - Animated logo with rotating rings
 * - Gradient text branding
 * - Feature pills highlighting key app capabilities
 * - Progress indicator with percentage
 * - Staggered entrance animations
 * - Smooth fade-out transition
 *
 * Total duration: ~4.5 seconds (3s loading + 0.5s hold + 1s fade-out)
 */
package com.example.cryptowallet.app.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cryptowallet.app.splash.components.*
import com.example.cryptowallet.theme.*
import kotlinx.coroutines.delay

/**
 * Splash screen composable with animated loading sequence.
 *
 * Orchestrates the entire splash screen experience including:
 * - Progress simulation (0% → 100% over 3 seconds)
 * - Hold at 100% for 500ms
 * - Fade-out animation over 1 second
 * - Navigation callback after completion
 *
 * All UI elements use staggered entrance animations for a progressive
 * reveal effect. The entire screen fades out smoothly before navigating
 * to the main app.
 *
 * @param onSplashComplete Callback invoked when splash sequence completes
 */
@Composable
fun SplashScreen(
    onSplashComplete: () -> Unit
) {
    var progress by remember { mutableStateOf(0f) }
    var alpha by remember { mutableStateOf(1f) }
    
    // Simulate loading progress
    LaunchedEffect(Unit) {
        while (progress < 1f) {
            delay(30) // Update every 30ms
            progress = (progress + 0.02f).coerceAtMost(1f) // Increment by 2%, cap at 100%
        }
        // Hold at 100% for 500ms
        delay(500)
        // Fade out
        alpha = 0f
        delay(1000) // Wait for fade animation
        onSplashComplete()
    }
    
    // Fade out animation
    val animatedAlpha by animateFloatAsState(
        targetValue = alpha,
        animationSpec = tween(durationMillis = 1000),
        label = "alpha"
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { this.alpha = animatedAlpha },
        contentAlignment = Alignment.Center
    ) {
        // Animated background
        AnimatedCryptoBackground()
        
        // Floating particles
        FloatingParticles(
            modifier = Modifier.fillMaxSize(),
            particleCount = 50 // Ultra fast streaming effect
        )
        
        // Main content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            // Animated logo
            AnimatedLogo()
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // App name with gradient
            GradientText(
                text = "CryptoVault",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Tagline with gradient
            GradientText(
                text = "Your Premium Crypto Companion",
                style = MaterialTheme.typography.titleLarge
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Feature pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FeaturePill(
                    icon = Icons.Default.Refresh,
                    text = "Real-time",
                    color = Yellow400
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                FeaturePill(
                    icon = Icons.Default.Lock,
                    text = "Secure",
                    color = Emerald400
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                FeaturePill(
                    icon = Icons.Default.Star,
                    text = "Analytics",
                    color = Blue400
                )
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Progress bar
            SplashProgressBar(progress = progress)
            
            Spacer(modifier = Modifier.height(64.dp))
            
            // Version info
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Version 1.0.0",
                    style = MaterialTheme.typography.labelSmall,
                    color = Slate600
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "© 2024 CryptoVault. All rights reserved.",
                    style = MaterialTheme.typography.labelSmall,
                    color = Slate600
                )
            }
        }
    }
}

/**
 * Feature pill component displaying an icon and text label.
 *
 * Small UI element that highlights a key app feature with:
 * - Icon in specified accent color
 * - Text label
 * - Semi-transparent background
 * - Subtle border
 *
 * Used to showcase "Real-time", "Secure", and "Analytics" features
 * on the splash screen.
 *
 * @param icon Material icon to display
 * @param text Feature label text
 * @param color Accent color for the icon
 */
@Composable
private fun FeaturePill(
    icon: ImageVector,
    text: String,
    color: Color
) {
    Row(
        modifier = Modifier
            .background(
                color = Slate800.copy(alpha = 0.3f),
                shape = RoundedCornerShape(20.dp)
            )
            .border(
                width = 1.dp,
                color = Slate700.copy(alpha = 0.5f),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = color,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp
            ),
            color = Slate300,
            maxLines = 1,
            softWrap = false
        )
    }
}
