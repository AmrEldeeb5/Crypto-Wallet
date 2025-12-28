package com.example.cryptovault.app.dca.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.cryptovault.app.components.*
import com.example.cryptovault.app.core.util.UiState
import com.example.cryptovault.app.dca.domain.DCAFrequency
import com.example.cryptovault.app.dca.domain.DCASchedule
import com.example.cryptovault.app.dca.domain.NextExecutionCalculator
import com.example.cryptovault.theme.*
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
    
    Scaffold(
        topBar = {
            ScreenHeader(
                title = "DCA Schedules",
                subtitle = "Dollar-Cost Averaging",
                onBackClick = onBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onEvent(DCAEvent.ShowCreateDialog) },
                containerColor = colors.accentBlue400,
                modifier = Modifier.semantics { contentDescription = "Create new DCA schedule" }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create DCA Schedule",
                    tint = colors.textPrimary
                )
            }
        },
        containerColor = colors.backgroundPrimary
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            DCAStatsHeader(
                totalInvested = state.totalInvested,
                activeCount = state.activeScheduleCount
            )
            
            ReminderBanner()
            
            when (val schedules = state.schedules) {
                is UiState.Loading -> DCALoadingContent()
                is UiState.Success -> {
                    DCAScheduleList(
                        schedules = schedules.data,
                        onEditClick = { viewModel.onEvent(DCAEvent.EditSchedule(it)) },
                        onDeleteClick = { viewModel.onEvent(DCAEvent.DeleteSchedule(it.id)) },
                        onToggleActive = { schedule, isActive ->
                            viewModel.onEvent(DCAEvent.ToggleScheduleActive(schedule.id, isActive))
                        }
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
                    EmptyState(
                        title = "No DCA Schedules",
                        description = "Create your first DCA schedule to start investing automatically.",
                        actionLabel = "Create Schedule",
                        onAction = { viewModel.onEvent(DCAEvent.ShowCreateDialog) },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
    
    if (state.showCreateDialog) {
        DCACreateDialog(
            formState = state.createFormState,
            isEditing = state.editingSchedule != null,
            onDismiss = { viewModel.onEvent(DCAEvent.HideCreateDialog) },
            onAmountChange = { viewModel.onEvent(DCAEvent.UpdateAmount(it)) },
            onFrequencyChange = { viewModel.onEvent(DCAEvent.UpdateFrequency(it)) },
            onDayOfWeekChange = { viewModel.onEvent(DCAEvent.UpdateDayOfWeek(it)) },
            onDayOfMonthChange = { viewModel.onEvent(DCAEvent.UpdateDayOfMonth(it)) },
            onSave = { viewModel.onEvent(DCAEvent.SaveSchedule) }
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
            .padding(horizontal = spacing.md)
            .clip(RoundedCornerShape(8.dp))
            .background(colors.accentBlue400.copy(alpha = 0.15f))
            .padding(spacing.sm)
    ) {
        Text(
            text = "Schedules are reminders only - trades must be executed manually",
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
    
    Box(modifier = modifier.clip(RoundedCornerShape(16.dp)).background(colors.cardBackground).padding(spacing.md)) {
        Column {
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
            Spacer(modifier = Modifier.height(spacing.xs))
            Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = colors.textPrimary)
        }
    }
}


@Composable
private fun DCAScheduleList(
    schedules: List<DCASchedule>,
    onEditClick: (DCASchedule) -> Unit,
    onDeleteClick: (DCASchedule) -> Unit,
    onToggleActive: (DCASchedule, Boolean) -> Unit
) {
    val spacing = LocalCryptoSpacing.current
    
    LazyColumn(
        contentPadding = PaddingValues(horizontal = spacing.md, vertical = spacing.sm),
        verticalArrangement = Arrangement.spacedBy(spacing.sm)
    ) {
        items(schedules, key = { it.id }) { schedule ->
            DCAScheduleCard(
                schedule = schedule,
                onEditClick = { onEditClick(schedule) },
                onDeleteClick = { onDeleteClick(schedule) },
                onToggleActive = { onToggleActive(schedule, it) }
            )
        }
    }
}

@Composable
private fun DCAScheduleCard(
    schedule: DCASchedule,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onToggleActive: (Boolean) -> Unit
) {
    val colors = LocalCryptoColors.current
    val spacing = LocalCryptoSpacing.current
    
    Box(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(colors.cardBackground).padding(spacing.md)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(spacing.md), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(colors.backgroundSecondary),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(model = schedule.coinIconUrl, contentDescription = "${schedule.coinName} icon", modifier = Modifier.size(36.dp))
                    }
                    Column {
                        Text(text = schedule.coinName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = colors.textPrimary)
                        Text(text = "$${formatAmount(schedule.amount)} ${schedule.frequency.displayName}", style = MaterialTheme.typography.bodyMedium, color = colors.textSecondary)
                    }
                }
                Switch(
                    checked = schedule.isActive,
                    onCheckedChange = onToggleActive,
                    colors = SwitchDefaults.colors(checkedThumbColor = colors.profit, checkedTrackColor = colors.profit.copy(alpha = 0.3f))
                )
            }
            
            Spacer(modifier = Modifier.height(spacing.sm))
            
            Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(colors.backgroundPrimary.copy(alpha = 0.5f)).padding(spacing.sm)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text(text = "Next", style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
                        Text(text = NextExecutionCalculator.formatNextExecutionDate(schedule.nextExecutionDate), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = colors.textPrimary)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Invested", style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
                        Text(text = "$${formatAmount(schedule.totalInvested)}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = colors.textPrimary)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "Executions", style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
                        Text(text = schedule.executionCount.toString(), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = colors.textPrimary)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(spacing.sm))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = onEditClick) { Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = colors.textSecondary) }
                IconButton(onClick = onDeleteClick) { Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = colors.loss) }
            }
        }
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DCACreateDialog(
    formState: DCACreateFormState,
    isEditing: Boolean,
    onDismiss: () -> Unit,
    onAmountChange: (String) -> Unit,
    onFrequencyChange: (DCAFrequency) -> Unit,
    onDayOfWeekChange: (Int) -> Unit,
    onDayOfMonthChange: (Int) -> Unit,
    onSave: () -> Unit
) {
    val colors = LocalCryptoColors.current
    val spacing = LocalCryptoSpacing.current
    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = if (isEditing) "Edit Schedule" else "Create Schedule", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
                if (formState.selectedCoinId.isNotEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
                        AsyncImage(model = formState.selectedCoinIconUrl, contentDescription = null, modifier = Modifier.size(32.dp))
                        Text(text = "${formState.selectedCoinName} (${formState.selectedCoinSymbol})", style = MaterialTheme.typography.bodyLarge)
                    }
                } else {
                    Text(text = "Select a coin first", style = MaterialTheme.typography.bodyMedium, color = colors.textSecondary)
                }
                
                OutlinedTextField(
                    value = formState.amount,
                    onValueChange = onAmountChange,
                    label = { Text("Amount (USD)") },
                    isError = formState.amountError != null,
                    supportingText = formState.amountError?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Text(text = "Frequency", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    DCAFrequency.entries.forEachIndexed { index, frequency ->
                        SegmentedButton(
                            selected = frequency == formState.frequency,
                            onClick = { onFrequencyChange(frequency) },
                            shape = SegmentedButtonDefaults.itemShape(index = index, count = DCAFrequency.entries.size)
                        ) { Text(text = frequency.displayName, style = MaterialTheme.typography.labelSmall) }
                    }
                }
                
                when (formState.frequency) {
                    DCAFrequency.WEEKLY, DCAFrequency.BIWEEKLY -> {
                        Text(text = "Day of Week", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                            daysOfWeek.forEachIndexed { index, day ->
                                SegmentedButton(
                                    selected = (index + 1) == formState.dayOfWeek,
                                    onClick = { onDayOfWeekChange(index + 1) },
                                    shape = SegmentedButtonDefaults.itemShape(index = index, count = daysOfWeek.size)
                                ) { Text(day.take(1), style = MaterialTheme.typography.labelSmall) }
                            }
                        }
                    }
                    DCAFrequency.MONTHLY -> {
                        Text(text = "Day of Month: ${formState.dayOfMonth}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                        Slider(value = formState.dayOfMonth.toFloat(), onValueChange = { onDayOfMonthChange(it.toInt()) }, valueRange = 1f..28f, steps = 26)
                    }
                    DCAFrequency.DAILY -> { }
                }
            }
        },
        confirmButton = {
            Button(onClick = onSave, enabled = formState.isValid, colors = ButtonDefaults.buttonColors(containerColor = colors.accentBlue400)) {
                Text(if (isEditing) "Update" else "Create")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
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
