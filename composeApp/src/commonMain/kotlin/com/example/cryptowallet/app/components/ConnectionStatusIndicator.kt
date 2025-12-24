package com.example.cryptowallet.app.components

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
import com.example.cryptowallet.app.realtime.domain.ConnectionState
import com.example.cryptowallet.theme.LocalCryptoColors
import com.example.cryptowallet.theme.LocalCryptoSpacing
import com.example.cryptowallet.theme.LocalCryptoTypography

/**
 * A component for displaying connection status.
 * Only visible when not connected.
 * 
 * @param connectionState The current connection state
 * @param modifier Modifier for the component
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
 * Returns true if the connection status indicator should be visible.
 */
fun shouldShowConnectionIndicator(connectionState: ConnectionState): Boolean {
    return connectionState != ConnectionState.CONNECTED && 
           connectionState != ConnectionState.DISCONNECTED
}
