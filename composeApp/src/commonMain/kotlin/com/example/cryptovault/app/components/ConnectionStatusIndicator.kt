/**
 * ConnectionStatusIndicator.kt
 *
 * Displays the current WebSocket connection status for real-time price updates.
 * Shows a banner when the connection is not in a healthy state.
 *
 * States displayed:
 * - Connecting: Blue indicator, "Connecting..." message
 * - Reconnecting: Blue indicator, "Reconnecting..." message
 * - Failed: Red indicator, "Offline - Using cached data" message
 * - Disconnected: Gray indicator, "Disconnected" message
 * - Connected: Hidden (no indicator shown)
 *
 * @see ConnectionState for all possible connection states
 * @see OfflineBanner for a simpler offline indicator
 */
package com.example.cryptovault.app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.cryptovault.app.realtime.domain.ConnectionState
import com.example.cryptovault.theme.LocalCryptoColors
import com.example.cryptovault.theme.LocalCryptoSpacing
import com.example.cryptovault.theme.LocalCryptoTypography

/**
 * Banner indicator showing WebSocket connection status.
 *
 * Displays a colored dot and status message when the connection
 * is not in a healthy (connected) state. Automatically hides
 * when connected.
 *
 * @param connectionState Current connection state from the real-time service
 * @param modifier Optional modifier for the indicator
 */
@Composable
fun ConnectionStatusIndicator(
    connectionState: ConnectionState,
    modifier: Modifier = Modifier
) {
    val colors = LocalCryptoColors.current
    val typography = LocalCryptoTypography.current
    val spacing = LocalCryptoSpacing.current
    
    // Only show when not connected
    if (connectionState == ConnectionState.CONNECTED) {
        return
    }
    
    val (text, indicatorColor) = when (connectionState) {
        ConnectionState.CONNECTED -> "Live" to colors.statusConnected
        ConnectionState.CONNECTING -> "Connecting..." to colors.statusConnecting
        ConnectionState.RECONNECTING -> "Reconnecting..." to colors.statusConnecting
        ConnectionState.FAILED -> "Offline - Using cached data" to colors.statusError
        ConnectionState.DISCONNECTED -> "Disconnected" to colors.textTertiary
    }
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(indicatorColor.copy(alpha = 0.1f))
            .padding(horizontal = spacing.md, vertical = spacing.xs)
            .semantics {
                contentDescription = "Connection status: $text"
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(indicatorColor)
        )
        Spacer(modifier = Modifier.width(spacing.xs))
        Text(
            text = text,
            style = typography.caption,
            color = indicatorColor
        )
    }
}

/**
 * Determines whether the connection indicator should be visible.
 *
 * The indicator is shown during connecting/reconnecting states and
 * when the connection has failed. It's hidden when connected or
 * intentionally disconnected.
 *
 * @param connectionState Current connection state
 * @return True if the indicator should be displayed
 */
fun shouldShowConnectionIndicator(connectionState: ConnectionState): Boolean {
    return connectionState != ConnectionState.CONNECTED && 
           connectionState != ConnectionState.DISCONNECTED
}
