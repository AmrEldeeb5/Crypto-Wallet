/**
 * ValguardHeader.kt
 *
 * Main app header component displaying the Valguard branding,
 * alert notifications, and optional menu access.
 *
 * Features:
 * - Gradient "Valguard" title text
 * - Subtitle with app description
 * - Alert bell icon with notification badge
 * - Optional "more" menu button
 * - Accessibility support for all interactive elements
 *
 * @see ScreenHeader for a simpler screen-level header
 */
package com.example.valguard.app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.valguard.theme.CryptoGradients
import com.example.valguard.theme.LocalCryptoColors
import org.jetbrains.compose.resources.painterResource
import valguard.composeapp.generated.resources.Res
import valguard.composeapp.generated.resources.material_symbols__notifications_outline
import valguard.composeapp.generated.resources.solar__bell_linear

/**
 * Main application header with branding and actions.
 *
 * Displays contextual market information with dynamic stats,
 * along with alert notifications and optional menu access.
 *
 * @param searchQuery Current search query
 * @param onSearchQueryChange Callback for search query changes
 * @param placeholder Placeholder text for search
 * @param alertCount Number of active alerts to show in badge (0 hides badge)
 * @param onAlertClick Callback when the alert bell is tapped
 * @param onMoreClick Optional callback for the more menu button (null hides button)
 * @param modifier Optional modifier for the header
 */
@Composable
fun ValguardHeader(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    placeholder: String = "Search...",
    alertCount: Int,
    onAlertClick: () -> Unit,
    onMoreClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val colors = LocalCryptoColors.current
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SearchBar(
            query = searchQuery,
            onQueryChange = onSearchQueryChange,
            placeholder = placeholder,
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp)
        )
        
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Alert bell with badge
            val hasAlerts = alertCount > 0
            val activeGradient = CryptoGradients.blueToPerple()
            
            Box(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        brush = activeGradient,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .background(
                        brush = if (hasAlerts) activeGradient else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent)),
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                IconButton(
                    onClick = onAlertClick,
                    modifier = Modifier.semantics { contentDescription = "Open alerts" }
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.solar__bell_linear),
                        contentDescription = "Alerts",
                        tint = if (hasAlerts) colors.textPrimary else colors.textSecondary,
                        modifier = Modifier.size(28.dp)
                    )
                }
                
                // Badge
                if (hasAlerts) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = (-4).dp, y = 4.dp)
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(colors.backgroundPrimary), // White/Dark background to contrast with gradient button
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (alertCount > 9) "9+" else alertCount.toString(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            style = androidx.compose.ui.text.TextStyle(
                                brush = activeGradient // Gradient text for the badge
                            )
                        )
                    }
                }
            }
            
            // More menu button
//            if (onMoreClick != null) {
//                IconButton(
//                    onClick = onMoreClick,
//                    modifier = Modifier.semantics { contentDescription = "Open menu" }
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.MoreVert,
//                        contentDescription = "More options",
//                        tint = colors.textSecondary,
//                        modifier = Modifier.size(24.dp)
//                    )
//                }
//            }
        }
    }
}
