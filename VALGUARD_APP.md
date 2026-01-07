# Valguard - Complete Application Documentation

## Why This Exists

Valguard was built to explore real-time data handling, cross-platform UI consistency, and financial UX patterns using Kotlin Multiplatform. It serves as a reference implementation for WebSocket integration, MVVM architecture, and property-based testing in a KMP environment.

---

## Overview

Valguard is a modern cryptocurrency portfolio tracking and management application built with Kotlin Multiplatform (KMP) and Compose Multiplatform. It features a premium dark gradient theme, real-time price updates via WebSocket, and comprehensive portfolio management tools.

**Platform**: Android & iOS (via Kotlin Multiplatform)  
**Architecture**: MVVM with Clean Architecture  
**UI Framework**: Compose Multiplatform  
**DI**: Koin  
**Database**: Room  
**Real-time**: WebSocket

---

## Non-Goals

This project explicitly does **not** include:

- ❌ Real-money trading or exchange integration
- ❌ On-chain wallet connectivity
- ❌ Custody of funds or private keys
- ❌ KYC/AML compliance features
- ❌ Payment processing
- ❌ Financial advice or recommendations

---

## Features

### Core Features
- **Real-time Price Tracking** - Live cryptocurrency prices via WebSocket
- **Portfolio Management** - Track holdings, value, and performance
- **Watchlist** - Save favorite coins for quick access
- **Search** - Find coins by name or symbol (case-insensitive)
- **Price Alerts UI** - Create and manage price alerts
- **Simulated Trading** - Portfolio-only buy/sell (no real execution, no exchange APIs)

> ⚠️ **Note**: All trading in Valguard is **simulated**. Transactions update local portfolio data only—no real cryptocurrency is bought, sold, or transferred. No exchange integration, no KYC, no compliance requirements.

### Additional Screens (Phase 2)
- **Coin Detail** - Full-screen modal with charts, stats, and trade actions
- **DCA (Dollar-Cost Averaging)** - Schedule recurring buy reminders
- **Compare** - Side-by-side coin comparison with metrics
- **Referral Program** - Share referral codes (UI-only MVP)
- **Leaderboard** - Portfolio performance rankings

---

## Design System

### Color Palette

| Color | Hex | Usage |
|-------|-----|-------|
| slate-950 | `#020617` | Primary background |
| slate-900 | `#0f172a` | Secondary background |
| slate-800/30 | - | Card backgrounds |
| blue-400 | `#60a5fa` | Primary accent |
| blue-500 | `#3b82f6` | Primary accent |
| blue-600 | `#2563eb` | Primary accent |
| purple-400 | `#c084fc` | Secondary accent |
| purple-500 | `#a855f7` | Secondary accent |
| purple-600 | `#9333ea` | Secondary accent |
| pink-400 | `#f472b6` | Gradient accent |
| emerald-400 | `#34d399` | Profit/positive |
| rose-400 | `#fb7185` | Loss/negative |
| white | `#ffffff` | Primary text |
| slate-400 | `#94a3b8` | Secondary text |

### Gradients
- **Brand Gradient**: blue-400 → purple-500 → pink-400
- **Active State**: blue-500 → purple-500
- **Profit Gradient**: emerald-400 with opacity
- **Loss Gradient**: rose-400 with opacity

### Typography
- Large titles: Bold, white
- Section headers: Semi-bold, white
- Body text: Regular, white/slate-400
- Labels: Medium, slate-400

---

## Architecture

### Project Structure

```
composeApp/src/commonMain/kotlin/com/example/Valguard/
├── App.kt                          # Application entry point
├── Platform.kt                     # Platform-specific declarations
├── app/
│   ├── coindetail/                 # Coin Detail feature
│   │   ├── domain/
│   │   │   └── CoinDetailData.kt
│   │   └── presentation/
│   │       ├── CoinDetailScreen.kt
│   │       └── CoinDetailViewModel.kt
│   │
│   ├── coins/                      # Coins feature
│   │   ├── data/
│   │   ├── domain/
│   │   └── presentation/
│   │
│   ├── compare/                    # Compare feature
│   │   ├── data/
│   │   ├── domain/
│   │   └── presentation/
│   │       ├── CompareScreen.kt
│   │       ├── CompareState.kt
│   │       └── CompareViewModel.kt
│   │
│   ├── components/                 # Shared UI components
│   │   ├── AlertModal.kt
│   │   ├── BottomNavigation.kt
│   │   ├── ChartPreview.kt
│   │   ├── CoinCard.kt
│   │   ├── ValguardHeader.kt
│   │   ├── EmptyState.kt
│   │   ├── ErrorState.kt
│   │   ├── ExpandableCoinCard.kt
│   │   ├── MoreMenuDropdown.kt
│   │   ├── OfflineBanner.kt
│   │   ├── PortfolioValueCard.kt
│   │   ├── SearchBar.kt
│   │   ├── Skeleton.kt
│   │   ├── TabNavigation.kt
│   │   └── ...
│   │
│   ├── core/                       # Core utilities
│   │   ├── database/               # Room database
│   │   ├── domain/                 # Shared domain models
│   │   ├── network/                # Network clients
│   │   └── util/                   # Utilities
│   │
│   ├── dca/                        # DCA feature
│   │   ├── data/
│   │   │   ├── DCARepository.kt
│   │   │   └── local/
│   │   │       └── DCAScheduleDao.kt
│   │   ├── domain/
│   │   │   └── NextExecutionCalculator.kt
│   │   └── presentation/
│   │       ├── DCAScreen.kt
│   │       └── DCAViewModel.kt
│   │
│   ├── di/                         # Dependency injection
│   │   └── Module.kt
│   │
│   ├── leaderboard/                # Leaderboard feature
│   │   └── presentation/
│   │       └── LeaderboardScreen.kt
│   │
│   ├── main/                       # Main screen
│   │   └── presentation/
│   │       └── MainScreen.kt
│   │
│   ├── mapper/                     # Data mappers
│   │   ├── CoinDetailsMapper.kt
│   │   └── CoinItemMapper.kt
│   │
│   ├── navigation/                 # Navigation
│   │   ├── BottomNavItem.kt
│   │   ├── Navigation.kt
│   │   ├── NavigationShell.kt
│   │   └── Screens.kt
│   │
│   ├── portfolio/                  # Portfolio feature
│   │   ├── data/
│   │   ├── domain/
│   │   └── presentation/
│   │
│   ├── realtime/                   # WebSocket real-time
│   │   ├── data/
│   │   └── domain/
│   │
│   ├── referral/                   # Referral feature
│   │   └── presentation/
│   │       └── ReferralScreen.kt
│   │
│   ├── trade/                      # Trading feature
│   │   ├── domain/
│   │   └── presentation/
│   │       └── common/
│   │           └── TradeScreen.kt
│   │
│   └── watchlist/                  # Watchlist feature
│       ├── data/
│       └── domain/
│
└── theme/                          # Theme system
    ├── CryptoColors.kt
    ├── CryptoGradients.kt
    ├── CryptoShapes.kt
    ├── CryptoSpacing.kt
    ├── CryptoTypography.kt
    └── Theme.kt
```

### State Management Pattern

```kotlin
// UiState sealed class for async operations
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String, val retry: (() -> Unit)? = null) : UiState<Nothing>()
    object Empty : UiState<Nothing>()
}
```

---

## Screens

### 1. Main Screen
The primary screen with three tabs:
- **Market Tab** - All available cryptocurrencies
- **Portfolio Tab** - Portfolio value card + owned coins
- **Watchlist Tab** - User's saved coins

**Components**: ValguardHeader, SearchBar, TabNavigation, CoinList, BottomNavigation

### 2. Coin Detail Screen
Full-screen modal showing comprehensive coin information:
- Header with coin icon, name, symbol
- Large price display with 24h change
- Price chart with timeframe selector (1H, 24H, 7D, 1M, 1Y)
- Statistics grid (Market Cap, Volume, Supply, ATH)
- Holdings section (if user owns coin)
- Buy/Sell action buttons

**Navigation**: Opens via coin card tap, dismisses with slide-down

### 3. DCA Screen
Dollar-Cost Averaging schedule management:
- List of existing schedules
- Schedule cards with coin info, amount, frequency, next date
- Active/paused toggle per schedule
- Create schedule form (coin selector, amount, frequency, day)
- Reminder banner: "Schedules are reminders only"

**Frequencies**: Daily, Weekly, Bi-weekly, Monthly

### 4. Compare Screen
Side-by-side cryptocurrency comparison:
- Two coin selector slots
- Searchable coin selection modal
- Comparison table with metrics:
  - Current Price
  - 24h Change
  - 7d Change
  - Market Cap
  - Volume
  - Circulating Supply
- Winner highlighting (emerald-400)
- Percentage differences
- Saved comparisons section

### 5. Referral Screen
Referral program UI (MVP):
- Referral code display: `CRYPTO` + last 6 chars of userId
- Copy to clipboard button
- Share button
- Mock statistics (invites sent, pending, completed)
- "Coming soon" banner
- Reward tiers with progress

### 6. Leaderboard Screen
Portfolio performance rankings:
- User position card at top
- Return percentage calculation: `((currentValue - totalInvested) / totalInvested) * 100`
- Timeframe selector (24H, 7D, 30D, All Time)
- Ranked list with mock data
- Top 3 special styling (🏆 🥈 🥉)
- Empty state for users without portfolio

### 7. Trade Screen (Simulated)
Portfolio-only buy/sell simulation:
- Coin header with current price
- Amount input
- Total calculation
- Confirmation dialog
- Updates local Room database only
- **No real exchange execution**

---

## Data Layer

### Room Entities

```kotlin
// Watchlist
@Entity(tableName = "watchlist")
data class WatchlistEntity(
    @PrimaryKey val coinId: String,
    val addedAt: Long
)

// DCA Schedules
@Entity(tableName = "dca_schedules")
data class DCAScheduleEntity(
    @PrimaryKey val id: String,
    val coinId: String,
    val coinName: String,
    val coinSymbol: String,
    val coinIconUrl: String,
    val amount: Double,
    val frequency: String,
    val dayOfWeek: Int?,
    val dayOfMonth: Int?,
    val isActive: Boolean,
    val createdAt: Long,
    val lastExecutionCheck: Long?
)

// DCA Executions
@Entity(tableName = "dca_executions")
data class DCAExecutionEntity(
    @PrimaryKey val id: String,
    val scheduleId: String,
    val scheduledDate: Long,
    val wasCompleted: Boolean,
    val completedAt: Long?
)

// Saved Comparisons
@Entity(tableName = "saved_comparisons")
data class SavedComparisonEntity(
    @PrimaryKey val id: String,
    val coin1Id: String,
    val coin1Symbol: String,
    val coin2Id: String,
    val coin2Symbol: String,
    val savedAt: Long
)

// Price Alerts
@Entity(tableName = "price_alerts")
data class PriceAlertEntity(
    @PrimaryKey val id: String,
    val coinId: String,
    val coinSymbol: String,
    val condition: String,
    val targetPrice: Double,
    val isActive: Boolean,
    val createdAt: Long
)
```

---

## Navigation

### Routes
- `main` - Main screen with tabs
- `coinDetail/{coinId}` - Coin detail modal
- `dca` - DCA schedules screen
- `compare` - Coin comparison screen
- `referral` - Referral program screen
- `leaderboard` - Performance rankings
- `buy/{coinId}` - Buy screen
- `sell/{coinId}` - Sell screen

### Bottom Navigation Items
- Market (home icon)
- Portfolio (wallet icon)
- Alerts (bell icon) → Opens AlertModal

### More Menu Items
- DCA
- Compare
- Leaderboard
- Referral

---

## Testing

### Test Coverage
- **244+ tests** across both phases
- Property-based tests for correctness properties
- Unit tests for ViewModels, Repositories, calculations

### Property Tests (Phase 1)
1. Theme Color Consistency
2. Search Filter Accuracy
3. Tab State Consistency
4. Coin Card Expansion State
5. Portfolio Value Calculation
6. Watchlist Persistence
7. Price Direction Indicator
8. Bottom Navigation State
9. Alert Badge Count
10. Portfolio Tab Filter

### Property Tests (Phase 2)
1. Price Change Color Mapping
2. DCA Amount Validation
3. DCA Schedule Persistence Round-Trip
4. DCA Frequency Form Fields
5. Next Execution Date Calculation
6. Comparison Metric Winner Determination
7. Comparison Percentage Difference Calculation
8. Saved Comparison Persistence Round-Trip
9. Referral Code Generation
10. Portfolio Return Calculation
11. Leaderboard Top 3 Styling
12. Holdings Display Condition
13. Offline Cache Fallback

---

## Accessibility

- All interactive elements have content descriptions
- WCAG AA contrast compliance (4.5:1 minimum)
- Minimum 48dp touch targets
- Screen reader support for loading/error states
- Chart alternatives with text descriptions

---

## WebSocket Lifecycle Management

### Connection Strategy
- **Auto-reconnect** with exponential backoff (1s → 2s → 4s → 8s → max 30s)
- **Max retry attempts**: 10 before showing persistent error state
- **Heartbeat**: Ping every 30s to detect stale connections

### App Lifecycle
- **Foreground**: Active connection, real-time updates
- **Background**: Socket paused after 30s, cached prices displayed
- **Resume**: Immediate reconnect attempt, snapshot refresh

### Update Throttling
- **Debounce**: 100ms minimum between UI updates per coin
- **Batch updates**: Aggregate multiple price changes within 50ms window
- **Priority**: Visible coins updated first, off-screen deferred

### Fallback Behavior
- **Disconnected**: Show last cached prices with "Offline" indicator
- **Reconnecting**: Show reconnecting spinner in status bar
- **Failed**: Error state with manual retry button

---

## Performance

- Initial content display within 300ms
- Real-time updates within 100ms of WebSocket message
- Lazy loading for all lists
- 60fps animations and scrolling
- Support for 100+ DCA schedules without degradation

---

## Error Handling

### Loading States
- Skeleton placeholders matching content layout
- Shimmer animation effect

### Error States
- Error message with retry button
- Rose-400 accent color
- Preserved form data on submission failure

### Empty States
- Contextual messages with CTAs
- Guides users to relevant actions

### Offline Support
- Cached data display with "Offline" indicator
- Graceful degradation when network unavailable

---

## Dependencies

```kotlin
// Core
kotlin = "2.1.x"
compose-multiplatform = "1.9.x"
koin = "4.0.x"

// Database
room = "2.7.x"
sqlite = "2.5.x"

// Network
ktor = "3.1.x"

// Image Loading
coil = "3.0.x"

// Testing
kotest = "5.9.x"
```

---

## Build & Run

```bash
# Android (Windows)
.\gradlew.bat :composeApp:assembleDebug

# Android (macOS/Linux)
./gradlew :composeApp:assembleDebug

# iOS (requires macOS & Xcode)
Open /iosApp in Xcode and run

# Run tests
./gradlew :composeApp:testDebugUnitTest
```

---

## Security & Production Status

✅ **Production Ready** (January 6, 2026)
- Cleartext traffic disabled
- ProGuard/R8 enabled
- Database migrations implemented
- Security grade: A (95/100)
- No hardcoded credentials

See [SECURITY_FIXES_APPLIED.md](./SECURITY_FIXES_APPLIED.md) for details.

---

## Documentation

- **[README.md](./README.md)** - Quick start guide
- **[PROJECT_PRESENTATION.md](./PROJECT_PRESENTATION.md)** - Comprehensive project overview
- **[DATABASE_MIGRATIONS.md](./composeApp/src/commonMain/kotlin/com/example/valguard/app/core/database/portfolio/DATABASE_MIGRATIONS.md)** - Migration guide
- **[VERIFICATION_REPORT.md](./VERIFICATION_REPORT.md)** - API integration verification
- **[REALITY_TESTING_GUIDE.md](./REALITY_TESTING_GUIDE.md)** - Testing procedures

---

## License

MIT License

---

**Last Updated**: January 6, 2026  
**Version**: 1.0.0  
**Status**: ✅ Production Ready
