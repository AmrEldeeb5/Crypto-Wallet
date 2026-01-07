# Database Migration Guide

## Overview

This document describes the database migration strategy for **PortfolioDatabase** in the Valguard app. Proper migrations ensure that user data is preserved when the database schema evolves.

---

## Current Status

✅ **Migrations Implemented**: Versions 5 → 6 → 7  
✅ **Data Preservation**: All user data is maintained during upgrades  
✅ **Fallback Strategy**: Removed (migrations handle all cases)

---

## Migration History

### Version 5 → 6 (MIGRATION_5_6)

**Date**: Initial portfolio features  
**Changes**:
- ✅ Added `PortfolioCoinEntity` table for tracking owned cryptocurrencies
- ✅ Added `UserBalanceEntity` table for cash balance tracking
- ✅ Added `coins` table for caching market data (CoinGecko API)
- ✅ Added `coin_details` table for detailed coin information

**Data Handling**:
- Initializes default user balance at $10,000
- Existing watchlist, DCA schedules, and saved comparisons are preserved

**Tables Added**:
```sql
PortfolioCoinEntity (coinId, name, symbol, iconUrl, averagePurchasePrice, amountOwned, timestamp)
UserBalanceEntity (id, cashBalance)
coins (id, symbol, name, image, currentPrice, marketCap, ...)
coin_details (id, description, lastUpdated)
```

---

### Version 6 → 7 (MIGRATION_6_7)

**Date**: Preferences system  
**Changes**:
- ✅ Added `preferences` table for app-wide settings
- ✅ Recovery migration for `PortfolioCoinEntity` (edge case handling)

**Data Handling**:
- All existing data is preserved
- Idempotent table creation (won't fail if table exists)

**Tables Added**:
```sql
preferences (`key`, value)
```

**Use Cases**:
- Onboarding completion tracking
- User settings and preferences
- Feature flags

---

## Database Schema (Version 7)

### Core Tables

| Table | Purpose | Key Fields |
|-------|---------|------------|
| `PortfolioCoinEntity` | User's owned cryptocurrencies | coinId, amountOwned, averagePurchasePrice |
| `UserBalanceEntity` | User's cash balance | id, cashBalance |
| `watchlist` | User's watchlist coins | coinId, addedAt |
| `coins` | Cached market data | id, currentPrice, marketCap, sparkline7d |
| `coin_details` | Detailed coin info | id, description |
| `preferences` | App settings | key, value |

### DCA (Dollar-Cost Averaging) Tables

| Table | Purpose | Relationships |
|-------|---------|---------------|
| `dca_schedules` | Recurring buy schedules | Parent table |
| `dca_executions` | Execution history | Foreign key → dca_schedules.id (CASCADE) |

### Feature Tables

| Table | Purpose |
|-------|---------|
| `saved_comparisons` | Coin comparison snapshots |

---

## Adding New Migrations

### Step 1: Update Database Version

```kotlin
@Database(
    entities = [...],
    version = 8  // Increment version
)
abstract class PortfolioDatabase: RoomDatabase() { ... }
```

### Step 2: Export Schema

Ensure `build.gradle.kts` has:
```kotlin
ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}
```

Run build to generate new schema JSON in `schemas/` directory.

### Step 3: Create Migration

Add new migration to `PortfolioDatabaseMigrations.kt`:

```kotlin
val MIGRATION_7_8 = object : Migration(7, 8) {
    override fun migrate(connection: SQLiteConnection) {
        // Add your migration SQL here
        connection.execSQL("""
            ALTER TABLE coins ADD COLUMN newField TEXT
        """.trimIndent())
    }
}
```

### Step 4: Register Migration

Add to `ALL_MIGRATIONS` array:
```kotlin
val ALL_MIGRATIONS = arrayOf(
    MIGRATION_5_6,
    MIGRATION_6_7,
    MIGRATION_7_8  // New migration
)
```

### Step 5: Test Migration

1. Create test in `PortfolioDatabaseMigrationTest.kt`
2. Run tests: `./gradlew :composeApp:testDebugUnitTest`
3. Test on device with old database version

---

## Best Practices

### ✅ DO

- **Preserve user data**: Always migrate existing data
- **Use transactions**: Migrations are automatically wrapped in transactions
- **Test thoroughly**: Test on devices with old app versions
- **Document changes**: Update this guide for each migration
- **Use idempotent SQL**: Use `IF NOT EXISTS` for safety
- **Handle nullable columns**: Provide defaults for new non-null columns

### ❌ DON'T

- **Don't use destructive migration** in production
- **Don't skip versions**: Migrations must form a continuous chain
- **Don't modify old migrations**: Once released, they're immutable
- **Don't forget indices**: Recreate indices if needed
- **Don't ignore foreign keys**: Respect referential integrity

---

## Common Migration Patterns

### Adding a Column

```kotlin
connection.execSQL("""
    ALTER TABLE tableName 
    ADD COLUMN newColumn TEXT DEFAULT 'default_value'
""".trimIndent())
```

### Creating a New Table

```kotlin
connection.execSQL("""
    CREATE TABLE IF NOT EXISTS newTable (
        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
        data TEXT NOT NULL
    )
""".trimIndent())
```

### Migrating Data

```kotlin
// Copy data to new structure
connection.execSQL("""
    INSERT INTO newTable (id, newField)
    SELECT id, oldField FROM oldTable
""".trimIndent())
```

### Dropping a Table (Careful!)

```kotlin
// Only if you're sure data isn't needed
connection.execSQL("DROP TABLE IF EXISTS oldTable")
```

---

## Testing Migrations

### Unit Tests

Run migration structure tests:
```bash
./gradlew :composeApp:testDebugUnitTest --tests "*MigrationTest"
```

### Integration Tests

1. Install old app version on device
2. Add test data
3. Install new app version
4. Verify data integrity

### Automated Testing

Use Room's `MigrationTestHelper` (Android):
```kotlin
@get:Rule
val helper = MigrationTestHelper(
    InstrumentationRegistry.getInstrumentation(),
    PortfolioDatabase::class.java
)
```

---

## Troubleshooting

### Migration Failed

**Symptom**: App crashes on startup with migration error

**Solutions**:
1. Check SQL syntax in migration
2. Verify migration chain is continuous
3. Check schema JSON files match
4. Test migration on clean database

### Data Loss

**Symptom**: User data missing after update

**Solutions**:
1. Review migration SQL - ensure data is copied
2. Check for DROP TABLE statements
3. Verify default values for new columns
4. Test with production-like data

### Schema Mismatch

**Symptom**: "Expected schema hash X, found Y"

**Solutions**:
1. Rebuild project: `./gradlew clean build`
2. Delete `schemas/` and regenerate
3. Verify `@Database` version matches migrations

---

## Resources

- [Room Migration Documentation](https://developer.android.com/training/data-storage/room/migrating-db-versions)
- [SQLite ALTER TABLE](https://www.sqlite.org/lang_altertable.html)
- Schema files: `composeApp/schemas/com.example.valguard.app.core.database.portfolio.PortfolioDatabase/`

---

## Version History

| Version | Date | Changes | Migration |
|---------|------|---------|-----------|
| 5 | Initial | Base schema (watchlist, DCA, comparisons) | - |
| 6 | Q4 2025 | Portfolio tracking, coin cache | MIGRATION_5_6 |
| 7 | Q1 2026 | Preferences system | MIGRATION_6_7 |

---

**Last Updated**: January 6, 2026  
**Maintainer**: Valguard Development Team

