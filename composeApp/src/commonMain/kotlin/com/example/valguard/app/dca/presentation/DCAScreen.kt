package com.example.valguard.app.dca.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.valguard.app.realtime.domain.PriceDirection
import coil3.compose.AsyncImage
import com.example.valguard.app.components.*
import com.example.valguard.app.core.util.UiState
import com.example.valguard.app.dca.domain.DCAFrequency
import com.example.valguard.app.dca.domain.DCASchedule
import com.example.valguard.app.dca.domain.NextExecutionCalculator
import com.example.valguard.theme.*
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.pow

@Composable
fun DCAScreen(
    onBack: () -> Unit,
    onNavigateToBuy: (String) -> Unit
) {
    val viewModel = koinViewModel<DCAViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = LocalCryptoColors.current
    
    // Convert available coins to UiCoinItem for the selector
    val selectorCoins = remember(state.availableCoins) {
        state.availableCoins.map { coin ->
            UiCoinItem(
                id = coin.id,
                name = coin.name,
                symbol = coin.symbol,
                iconUrl = coin.iconUrl,
                formattedPrice = coin.price?.let { "$${formatAmount(it)}" } ?: "-",
                formattedChange = "-",
                changePercent = 0.0,
                isPositive = true,
                priceDirection = PriceDirection.UNCHANGED
            )
        }
    }
    
    Scaffold(
//        topBar = {
//            ScreenHeader(
//                title = "Dollar-Cost Averaging",
//                subtitle = "Automate your crypto investments",
//                onBackClick = onBack
//            )
//        },
        containerColor = colors.backgroundPrimary
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
//            DCAStatsHeader(
//                totalInvested = state.totalInvested,
//                activeCount = state.activeScheduleCount
//            )
            
            //ReminderBanner()
            
            when (val schedules = state.schedules) {
                is UiState.Loading -> DCALoadingContent()
                is UiState.Success -> {
                    DCAScheduleList(
                        schedules = schedules.data,
                        formState = state.createFormState,
                        isEditing = state.editingSchedule != null,
                        onEditClick = { viewModel.onEvent(DCAEvent.EditSchedule(it)) },
                        onDeleteClick = { viewModel.onEvent(DCAEvent.DeleteSchedule(it.toLong())) }, // Assuming id should be Long
                        onToggleActive = { scheduleId, isActive ->
                            viewModel.onEvent(DCAEvent.ToggleScheduleActive(scheduleId.toLong(), isActive))
                        },
                        onAmountChange = { viewModel.onEvent(DCAEvent.UpdateAmount(it)) },
                        onFrequencyChange = { viewModel.onEvent(DCAEvent.UpdateFrequency(it)) },
                        onDayOfWeekChange = { viewModel.onEvent(DCAEvent.UpdateDayOfWeek(it)) },
                        onDayOfMonthChange = { viewModel.onEvent(DCAEvent.UpdateDayOfMonth(it)) },
                        onCoinClick = { viewModel.onEvent(DCAEvent.ShowCoinSelector) },
                        onSaveClick = { viewModel.onEvent(DCAEvent.SaveSchedule) },
                        onCancelEdit = { viewModel.onEvent(DCAEvent.CancelEdit) },
                        onCardClick = { viewModel.onEvent(DCAEvent.ShowActionSheet(it)) }
                    )
                }
                is UiState.Error -> {
                    ErrorState(
                        message = schedules.message,
                        onRetry = { viewModel.onEvent(DCAEvent.Retry) },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                is UiState.Empty -> {
                    DCAScheduleList(
                        schedules = emptyList(),
                        formState = state.createFormState,
                        isEditing = state.editingSchedule != null,
                        onEditClick = {},
                        onDeleteClick = {},
                        onToggleActive = { _, _ -> },
                        onAmountChange = { viewModel.onEvent(DCAEvent.UpdateAmount(it)) },
                        onFrequencyChange = { viewModel.onEvent(DCAEvent.UpdateFrequency(it)) },
                        onDayOfWeekChange = { viewModel.onEvent(DCAEvent.UpdateDayOfWeek(it)) },
                        onDayOfMonthChange = { viewModel.onEvent(DCAEvent.UpdateDayOfMonth(it)) },
                        onCoinClick = { viewModel.onEvent(DCAEvent.ShowCoinSelector) },
                        onSaveClick = { viewModel.onEvent(DCAEvent.SaveSchedule) },
                        onCancelEdit = {},
                        onCardClick = {}
                    )
                }
            }
        }
    }
    
    if (state.showUpgradePrompt) {
        UpgradePromptDialog(
            onDismiss = { viewModel.onEvent(DCAEvent.DismissUpgradePrompt) },
            onUpgrade = {
                // TODO: Navigate to upgrade screen
                viewModel.onEvent(DCAEvent.DismissUpgradePrompt)
            }
        )
    }
    
    state.selectedScheduleForAction?.let { schedule ->
        if (state.showActionSheet) {
            ActionBottomSheet(
                schedule = schedule,
                onDismiss = { viewModel.onEvent(DCAEvent.HideActionSheet) },
                onEdit = { viewModel.onEvent(DCAEvent.EditSchedule(schedule)) },
                onDelete = { viewModel.onEvent(DCAEvent.DeleteSchedule(schedule.id)) }
            )
        }
    }
    
    if (state.showCoinSelector) {
        CoinSelectorBottomSheet(
            coins = selectorCoins,
            onDismiss = { viewModel.onEvent(DCAEvent.HideCoinSelector) },
            onCoinSelected = { id, name, symbol, iconUrl ->
                viewModel.onEvent(DCAEvent.SelectCoin(id, name, symbol, iconUrl))
            }
        )
    }
}

@Composable
private fun ReminderBanner() {
    val colors = LocalCryptoColors.current
    val spacing = LocalCryptoSpacing.current
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.md, vertical = spacing.xs)
            .clip(RoundedCornerShape(8.dp))
            .background(colors.accentBlue400.copy(alpha = 0.15f))
            .padding(spacing.sm)
    ) {
        Text(
            text = "DCA schedules are reminders — trades must be confirmed manually",
            style = MaterialTheme.typography.bodySmall,
            color = colors.accentBlue400
        )
    }
}

@Composable
private fun DCAStatsHeader(totalInvested: Double, activeCount: Int) {
    val spacing = LocalCryptoSpacing.current
    
    Row(
        modifier = Modifier.fillMaxWidth().padding(spacing.md),
        horizontalArrangement = Arrangement.spacedBy(spacing.md)
    ) {
        DCAStatCard(label = "Total Invested", value = "$${formatAmount(totalInvested)}", modifier = Modifier.weight(1f))
        DCAStatCard(label = "Active Schedules", value = activeCount.toString(), modifier = Modifier.weight(1f))
    }
}

@Composable
private fun DCAStatCard(label: String, value: String, modifier: Modifier = Modifier) {
    val colors = LocalCryptoColors.current
    val spacing = LocalCryptoSpacing.current
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(colors.cardBackground.copy(alpha = 0.6f))
            .border(
                width = 1.dp,
                color = colors.textSecondary.copy(alpha = 0.1f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(spacing.lg)
    ) {
        Column {
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
            Spacer(modifier = Modifier.height(spacing.sm))
            Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = colors.textPrimary)
        }
    }
}


@Composable
private fun DCAScheduleList(
    schedules: List<DCASchedule>,
    formState: DCACreateFormState,
    isEditing: Boolean,
    onEditClick: (DCASchedule) -> Unit,
    onDeleteClick: (Long) -> Unit,
    onToggleActive: (Long, Boolean) -> Unit,
    onAmountChange: (String) -> Unit,
    onFrequencyChange: (DCAFrequency) -> Unit,
    onDayOfWeekChange: (Int) -> Unit,
    onDayOfMonthChange: (Int) -> Unit,
    onCoinClick: () -> Unit,
    onSaveClick: () -> Unit,
    onCancelEdit: () -> Unit,
    onCardClick: (DCASchedule) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalCryptoSpacing.current
    
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = spacing.md,
            end = spacing.md, 
            top = spacing.md,
            bottom = 100.dp // Extra space for bottom nav
        ),
        verticalArrangement = Arrangement.spacedBy(spacing.md)
    ) {
        // Create/Edit form always at the top
        item {
            CreateNewScheduleSection(
                formState = formState,
                isEditing = isEditing,
                onAmountChange = onAmountChange,
                onFrequencyChange = onFrequencyChange,
                onDayOfWeekChange = onDayOfWeekChange,
                onDayOfMonthChange = onDayOfMonthChange,
                onSave = onSaveClick,
                onSelectCoin = onCoinClick,
                onCancelEdit = onCancelEdit
            )
        }
        
        if (schedules.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(spacing.sm))
            }
            
            items(schedules, key = { it.id }) { schedule ->
                DCAScheduleCard(
                    schedule = schedule,
                    onEditClick = { onEditClick(schedule) },
                    onDeleteClick = { onDeleteClick(schedule.id) },
                    onToggleActive = { onToggleActive(schedule.id, it) },
                    onCardClick = { onCardClick(schedule) }
                )
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(spacing.xl))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateNewScheduleSection(
    formState: DCACreateFormState,
    isEditing: Boolean,
    onAmountChange: (String) -> Unit,
    onFrequencyChange: (DCAFrequency) -> Unit,
    onDayOfWeekChange: (Int) -> Unit,
    onDayOfMonthChange: (Int) -> Unit,
    onSave: () -> Unit,
    onSelectCoin: () -> Unit,
    onCancelEdit: () -> Unit
) {
    val colors = LocalCryptoColors.current
    val spacing = LocalCryptoSpacing.current
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(colors.cardBackground.copy(alpha = 0.4f))
            .border(
                width = 1.dp,
                color = colors.border.copy(alpha = 0.1f),
                shape = RoundedCornerShape(32.dp)
            )
            .padding(spacing.xl)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isEditing) "Edit Schedule" else "Create New Schedule",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
            
            if (isEditing) {
                IconButton(onClick = onCancelEdit) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cancel Edit",
                        tint = colors.textSecondary
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(spacing.lg))
        
        Text(
            text = "Amount per Purchase",
            style = MaterialTheme.typography.bodySmall,
            color = colors.textTertiary
        )
        
        Spacer(modifier = Modifier.height(spacing.sm))
        
        DCAAmountInput(
            value = formState.amount,
            onValueChange = onAmountChange,
            isError = formState.amountError != null
        )
        
        Spacer(modifier = Modifier.height(spacing.lg))
        
        // Coin Selector if no coin selected or if user wants to change
        OutlinedButton(
            onClick = onSelectCoin,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = colors.textPrimary
            ),
            border = BorderStroke(1.dp, colors.border.copy(alpha = 0.2f))
        ) {
            if (formState.selectedCoinId.isNotEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm)
                ) {
                    AsyncImage(
                        model = formState.selectedCoinIconUrl,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(text = "${formState.selectedCoinName} (${formState.selectedCoinSymbol})")
                }
            } else {
                Text(text = "Choose a coin")
            }
        }
        
        Spacer(modifier = Modifier.height(spacing.lg))
        
        Text(
            text = "Frequency",
            style = MaterialTheme.typography.bodySmall,
            color = colors.textTertiary
        )
        
        Spacer(modifier = Modifier.height(spacing.sm))
        
        FrequencySelector(
            selectedFrequency = formState.frequency,
            onFrequencyChange = onFrequencyChange
        )
        
        // Day of week selector for Weekly
        if (formState.frequency == DCAFrequency.WEEKLY) {
            Spacer(modifier = Modifier.height(spacing.md))
            Text(
                text = "Day of week",
                style = MaterialTheme.typography.bodySmall,
                color = colors.textTertiary
            )
            Spacer(modifier = Modifier.height(spacing.sm))
            // Simple row for now, could be improved
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf(1, 2, 3, 4, 5, 6, 7).forEach { day ->
                    val isSelected = formState.dayOfWeek == day
                    Surface(
                        onClick = { onDayOfWeekChange(day) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) colors.accentBlue400 else colors.backgroundSecondary,
                        border = if (isSelected) null else BorderStroke(1.dp, colors.border.copy(alpha = 0.1f))
                    ) {
                        Text(
                            text = when(day) {
                                1 -> "M"
                                2 -> "T"
                                3 -> "W"
                                4 -> "T"
                                5 -> "F"
                                6 -> "S"
                                7 -> "S"
                                else -> ""
                            },
                            modifier = Modifier.padding(vertical = 8.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) Color.White else colors.textPrimary
                        )
                    }
                }
            }
        }
        
        // Day of month selector for Monthly
        if (formState.frequency == DCAFrequency.MONTHLY) {
            Spacer(modifier = Modifier.height(spacing.md))
            Text(
                text = "Day of month (1-28)",
                style = MaterialTheme.typography.bodySmall,
                color = colors.textTertiary
            )
            Spacer(modifier = Modifier.height(spacing.sm))
            // This is a bit complex for a simple row, but let's just use a basic input or small grid
            // For now, just a placeholder or very basic implementation
            OutlinedTextField(
                value = formState.dayOfMonth.toString(),
                onValueChange = { val v = it.toIntOrNull(); if (v != null && v in 1..28) onDayOfMonthChange(v) },
                modifier = Modifier.width(80.dp),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                textStyle = MaterialTheme.typography.bodyMedium,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
        
        Spacer(modifier = Modifier.height(spacing.xl))
        
        Button(
            onClick = onSave,
            enabled = formState.isValid,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.White
            ),
            contentPadding = PaddingValues(0.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF3B82F6), // Blue
                                Color(0xFF8B5CF6)  // Purple/Violet
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isEditing) Icons.Default.Edit else Icons.Default.Add,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isEditing) "Update Recurring Buy" else "Create Recurring Buy",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun DCAAmountInput(
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean
) {
    val colors = LocalCryptoColors.current
    
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        textStyle = MaterialTheme.typography.headlineMedium.copy(
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        prefix = {
            Text(
                text = "$ ",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = colors.textTertiary
                )
            )
        },
        placeholder = {
            Text(
                text = "0",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = colors.textTertiary.copy(alpha = 0.3f)
                )
            )
        },
        isError = isError,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Slate900.copy(alpha = 0.5f),
            focusedContainerColor = Slate900.copy(alpha = 0.5f),
            unfocusedBorderColor = colors.border.copy(alpha = 0.1f),
            focusedBorderColor = colors.accentBlue400
        )
    )
}

@Composable
private fun FrequencySelector(
    selectedFrequency: DCAFrequency,
    onFrequencyChange: (DCAFrequency) -> Unit
) {
    val frequencies = DCAFrequency.entries
    val colors = LocalCryptoColors.current
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        frequencies.forEach { frequency ->
            val isSelected = frequency == selectedFrequency
            
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isSelected) {
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF3B82F6),
                                    Color(0xFF8B5CF6)
                                )
                            )
                        } else {
                            SolidColor(Color(0xFF1E293B).copy(alpha = 0.5f))
                        }
                    )
                    .border(
                        width = 1.dp,
                        color = if (isSelected) Color.Transparent else colors.border.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onFrequencyChange(frequency) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = frequency.displayName,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) Color.White else colors.textSecondary
                )
            }
        }
    }
}

@Composable
private fun DCAScheduleCard(
    schedule: DCASchedule,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onToggleActive: (Boolean) -> Unit,
    onCardClick: () -> Unit
) {
    val colors = LocalCryptoColors.current
    val spacing = LocalCryptoSpacing.current
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onCardClick)
            .background(colors.cardBackground.copy(alpha = 0.4f))
            .border(
                width = 1.dp,
                color = colors.border.copy(alpha = 0.1f),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(spacing.lg)
            .semantics(mergeDescendants = true) {
                contentDescription = "${schedule.coinName} Auto-Buy. " +
                    "${formatAmount(schedule.amount)} ${schedule.frequency.displayName}. " +
                    "Status: ${if (schedule.isActive) "active" else "paused"}. " +
                    "Next buy: ${NextExecutionCalculator.formatNextExecutionDate(schedule.nextExecution())}. " +
                    "Total invested: ${formatAmount(schedule.totalInvested)}. " +
                    "Frequency: ${schedule.frequency.displayName}"
            }
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(spacing.md), verticalAlignment = Alignment.CenterVertically) {
                    CoinIconBox(
                        iconUrl = schedule.coinIconUrl,
                        contentDescription = null,
                        size = 56.dp,
                        iconSize = 36.dp,
                        cornerRadius = 16.dp,
                        borderColor = colors.accentPurple400
                    )
                    Column {
                        Text(
                            text = "${schedule.coinSymbol} Auto-Buy",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "$${formatAmount(schedule.amount)} · ${schedule.frequency.displayName}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.textSecondary
                        )
                    }
                }
                StatusBadge(
                    text = if (schedule.isActive) "active" else "paused",
                    isActive = schedule.isActive
                )
            }
            
            Spacer(modifier = Modifier.height(spacing.xl))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DetailColumn(
                    label = "Next Buy",
                    value = NextExecutionCalculator.formatNextExecutionDate(schedule.nextExecution()),
                    alignment = Alignment.Start
                )
                DetailColumn(
                    label = "Total Invested",
                    value = "$${formatAmount(schedule.totalInvested)}",
                    alignment = Alignment.CenterHorizontally
                )
                DetailColumn(
                    label = "Frequency",
                    value = schedule.frequency.displayName,
                    alignment = Alignment.End
                )
            }
        }
    }
}

@Composable
private fun DetailColumn(
    label: String,
    value: String,
    alignment: Alignment.Horizontal
) {
    val colors = LocalCryptoColors.current
    Column(horizontalAlignment = alignment) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = colors.textTertiary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = colors.textPrimary
        )
    }
}

@Composable
private fun StatusBadge(text: String, isActive: Boolean) {
    val colors = LocalCryptoColors.current
    val backgroundColor = if (isActive) Color(0xFF10463A) else Color(0xFF463A10)
    val textColor = if (isActive) Color(0xFF34D399) else Color(0xFFFBBF24)
    
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(100.dp))
            .background(backgroundColor.copy(alpha = 0.5f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
private fun DCALoadingContent() {
    val spacing = LocalCryptoSpacing.current
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = spacing.md)) {
        repeat(3) {
            SkeletonDCACard()
            Spacer(modifier = Modifier.height(spacing.sm))
        }
    }
}

@Composable
private fun SkeletonDCACard() {
    val colors = LocalCryptoColors.current
    val spacing = LocalCryptoSpacing.current
    
    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(colors.cardBackground).padding(spacing.md)) {
        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(horizontalArrangement = Arrangement.spacedBy(spacing.md), verticalAlignment = Alignment.CenterVertically) {
                    SkeletonBox(modifier = Modifier.size(48.dp), shape = RoundedCornerShape(12.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                        SkeletonText(width = 100.dp, height = 18.dp)
                        SkeletonText(width = 80.dp, height = 14.dp)
                    }
                }
                SkeletonBox(modifier = Modifier.size(40.dp, 24.dp), shape = RoundedCornerShape(12.dp))
            }
            Spacer(modifier = Modifier.height(spacing.sm))
            SkeletonBox(modifier = Modifier.fillMaxWidth().height(60.dp), shape = RoundedCornerShape(12.dp))
        }
    }
}



private fun formatAmount(amount: Double): String = formatDecimal(amount, 2)

private fun formatDecimal(value: Double, decimals: Int): String {
    val factor = 10.0.pow(decimals)
    val rounded = kotlin.math.round(value * factor) / factor
    val parts = rounded.toString().split(".")
    val intPart = parts[0]
    val decPart = if (parts.size > 1) parts[1].take(decimals).padEnd(decimals, '0') else "0".repeat(decimals)
    return "$intPart.$decPart"
}


@Composable
private fun UpgradePromptDialog(
    onDismiss: () -> Unit,
    onUpgrade: () -> Unit
) {
    val colors = LocalCryptoColors.current
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Upgrade to Premium",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = "Free tier limited to 3 active schedules. Upgrade to Premium for unlimited schedules and more features.",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(
                onClick = onUpgrade,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.accentBlue400
                )
            ) {
                Text("Upgrade")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ActionBottomSheet(
    schedule: DCASchedule,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val colors = LocalCryptoColors.current
    val spacing = LocalCryptoSpacing.current
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = colors.backgroundPrimary,
        dragHandle = { BottomSheetDefaults.DragHandle(color = colors.border.copy(alpha = 0.3f)) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.md, vertical = spacing.lg)
        ) {
            Text(
                text = "${schedule.coinName} Schedule",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                modifier = Modifier.padding(bottom = spacing.lg)
            )
            
            // Edit action
            Surface(
                onClick = {
                    onEdit()
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                color = colors.cardBackground.copy(alpha = 0.4f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(spacing.md),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(colors.accentBlue400.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = colors.accentBlue400,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(spacing.md))
                    Text(
                        text = "Edit Schedule",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = colors.textPrimary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(spacing.sm))
            
            // Delete action
            Surface(
                onClick = {
                    onDelete()
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                color = colors.cardBackground.copy(alpha = 0.4f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(spacing.md),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(colors.loss.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = colors.loss,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(spacing.md))
                    Text(
                        text = "Delete Schedule",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = colors.loss
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(spacing.xl))
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CoinSelectorBottomSheet(
    coins: List<UiCoinItem>,
    onDismiss: () -> Unit,
    onCoinSelected: (String, String, String, String) -> Unit
) {
    val colors = LocalCryptoColors.current
    val spacing = LocalCryptoSpacing.current
    var searchQuery by remember { mutableStateOf("") }
    
    val filteredCoins = remember(coins, searchQuery) {
        if (searchQuery.isEmpty()) {
            coins
        } else {
            coins.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                it.symbol.contains(searchQuery, ignoreCase = true)
            }
        }
    }
    
    val popularCoins = remember(coins) {
        listOf("BTC", "ETH", "USDT", "USDC", "BNB").mapNotNull { symbol ->
            coins.find { it.symbol.equals(symbol, ignoreCase = true) }
        }
    }
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = colors.backgroundPrimary,
        modifier = Modifier.fillMaxHeight(),
        dragHandle = { BottomSheetDefaults.DragHandle(color = colors.border.copy(alpha = 0.3f)) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = spacing.md)
        ) {
            Text(
                text = "Choose a coin",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                modifier = Modifier.padding(bottom = spacing.md)
            )
            
            // Search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { 
                    Text(
                        text = "Search coins...",
                        color = colors.textTertiary.copy(alpha = 0.5f)
                    ) 
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = spacing.md),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Slate900.copy(alpha = 0.5f),
                    focusedContainerColor = Slate900.copy(alpha = 0.5f),
                    unfocusedBorderColor = colors.border.copy(alpha = 0.1f),
                    focusedBorderColor = colors.accentBlue400,
                    cursorColor = colors.accentBlue400
                )
            )
            
            // Popular coins section
            if (searchQuery.isEmpty() && popularCoins.isNotEmpty()) {
                Text(
                    text = "Popular",
                    style = MaterialTheme.typography.labelLarge,
                    color = colors.textSecondary,
                    modifier = Modifier.padding(bottom = spacing.sm)
                )
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = spacing.lg),
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm)
                ) {
                    popularCoins.take(5).forEach { coin ->
                        Surface(
                            onClick = {
                                onCoinSelected(coin.id, coin.name, coin.symbol, coin.iconUrl)
                                onDismiss()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            color = colors.cardBackground.copy(alpha = 0.4f),
                            border = BorderStroke(1.dp, colors.border.copy(alpha = 0.1f))
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = spacing.md),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CoinIconBox(
                                    iconUrl = coin.iconUrl,
                                    contentDescription = null,
                                    size = 40.dp,
                                    iconSize = 24.dp,
                                    cornerRadius = 12.dp,
                                    borderColor = colors.accentPurple400
                                )
                                Spacer(modifier = Modifier.height(spacing.sm))
                                Text(
                                    text = coin.symbol,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.textPrimary
                                )
                            }
                        }
                    }
                }
            }
            
            // Full coin list
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(spacing.xs),
                contentPadding = PaddingValues(bottom = spacing.xl)
            ) {
                items(filteredCoins, key = { it.id }) { coin ->
                    Surface(
                        onClick = {
                            onCoinSelected(coin.id, coin.name, coin.symbol, coin.iconUrl)
                            onDismiss()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp)),
                        color = Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(spacing.sm),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CoinIconBox(
                                iconUrl = coin.iconUrl,
                                contentDescription = null,
                                size = 44.dp,
                                iconSize = 28.dp,
                                cornerRadius = 10.dp,
                                borderColor = colors.accentPurple400
                            )
                            Spacer(modifier = Modifier.width(spacing.md))
                            Column {
                                Text(
                                    text = coin.symbol,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.textPrimary
                                )
                                Text(
                                    text = coin.name,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = colors.textTertiary
                                )
                            }
                        }
                    }
                }
                
                if (filteredCoins.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(spacing.xl),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No coins found",
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.textTertiary
                            )
                        }
                    }
                }
            }
        }
    }
}
