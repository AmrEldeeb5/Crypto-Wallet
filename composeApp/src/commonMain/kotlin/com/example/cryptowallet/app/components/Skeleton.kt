package com.example.cryptowallet.app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.cryptowallet.theme.LocalCryptoColors

@Composable
fun SkeletonBox(modifier: Modifier = Modifier, shape: Shape = RoundedCornerShape(8.dp)) {
    Box(modifier = modifier.clip(shape).background(shimmerBrush()))
}

@Composable
fun SkeletonText(width: Dp, modifier: Modifier = Modifier, height: Dp = 16.dp) {
    SkeletonBox(modifier = modifier.width(width).height(height), shape = RoundedCornerShape(4.dp))
}

@Composable
fun SkeletonCircle(size: Dp, modifier: Modifier = Modifier) {
    SkeletonBox(modifier = modifier.size(size), shape = CircleShape)
}

@Composable
fun SkeletonCard(modifier: Modifier = Modifier) {
    val colors = LocalCryptoColors.current
    Box(modifier = modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(colors.cardBackground).padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SkeletonBox(modifier = Modifier.size(48.dp), shape = RoundedCornerShape(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                SkeletonText(width = 100.dp, height = 18.dp)
                SkeletonText(width = 60.dp, height = 14.dp)
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SkeletonText(width = 80.dp, height = 18.dp)
                SkeletonText(width = 50.dp, height = 14.dp)
            }
        }
    }
}

@Composable
fun SkeletonCoinDetailHeader(modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            SkeletonBox(modifier = Modifier.size(64.dp), shape = RoundedCornerShape(16.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SkeletonText(width = 120.dp, height = 28.dp)
                SkeletonText(width = 60.dp, height = 16.dp)
            }
        }
        SkeletonBox(modifier = Modifier.size(48.dp), shape = RoundedCornerShape(12.dp))
    }
}
