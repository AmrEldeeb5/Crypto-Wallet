package com.example.valguard.app.core.database.portfolio

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

/**
 * Database migrations for PortfolioDatabase.
 *
 * Each migration preserves user data while updating the schema.
 * Migrations are applied sequentially from the installed version to the current version.
 *
 * @see PortfolioDatabase for current schema version
 */
object PortfolioDatabaseMigrations {

    /**
     * Migration from version 5 to 6.
     *
     * Changes:
     * - Added PortfolioCoinEntity table for tracking owned coins
     * - Added UserBalanceEntity table for cash balance
     * - Added coins table for cached coin data
     * - Added coin_details table for detailed coin information
     */
    val MIGRATION_5_6 = object : Migration(5, 6) {
        override fun migrate(connection: SQLiteConnection) {
            // Create PortfolioCoinEntity table
            connection.execSQL("""
                CREATE TABLE IF NOT EXISTS `PortfolioCoinEntity` (
                    `coinId` TEXT NOT NULL,
                    `name` TEXT NOT NULL,
                    `symbol` TEXT NOT NULL,
                    `iconUrl` TEXT NOT NULL,
                    `averagePurchasePrice` REAL NOT NULL,
                    `amountOwned` REAL NOT NULL,
                    `timestamp` INTEGER NOT NULL,
                    PRIMARY KEY(`coinId`)
                )
            """.trimIndent())

            // Create UserBalanceEntity table
            connection.execSQL("""
                CREATE TABLE IF NOT EXISTS `UserBalanceEntity` (
                    `id` TEXT NOT NULL,
                    `cashBalance` REAL NOT NULL,
                    PRIMARY KEY(`id`)
                )
            """.trimIndent())

            // Initialize with default balance of $10,000
            connection.execSQL("""
                INSERT INTO UserBalanceEntity (id, cashBalance) 
                VALUES ('default_user', 10000.0)
            """.trimIndent())

            // Create coins table for cached market data
            connection.execSQL("""
                CREATE TABLE IF NOT EXISTS `coins` (
                    `id` TEXT NOT NULL,
                    `symbol` TEXT NOT NULL,
                    `name` TEXT NOT NULL,
                    `image` TEXT,
                    `currentPrice` REAL,
                    `marketCap` REAL,
                    `marketCapRank` INTEGER,
                    `totalVolume` REAL,
                    `high24h` REAL,
                    `low24h` REAL,
                    `priceChange24h` REAL,
                    `priceChangePercentage24h` REAL,
                    `circulatingSupply` REAL,
                    `totalSupply` REAL,
                    `maxSupply` REAL,
                    `ath` REAL,
                    `atl` REAL,
                    `sparkline7d` TEXT,
                    `lastUpdated` INTEGER NOT NULL,
                    PRIMARY KEY(`id`)
                )
            """.trimIndent())

            // Create coin_details table
            connection.execSQL("""
                CREATE TABLE IF NOT EXISTS `coin_details` (
                    `id` TEXT NOT NULL,
                    `description` TEXT,
                    `lastUpdated` INTEGER NOT NULL,
                    PRIMARY KEY(`id`)
                )
            """.trimIndent())
        }
    }

    /**
     * Migration from version 6 to 7.
     *
     * Changes:
     * - Added preferences table for app settings (e.g., onboarding completion)
     * - Ensured PortfolioCoinEntity exists (recovery migration)
     */
    val MIGRATION_6_7 = object : Migration(6, 7) {
        override fun migrate(connection: SQLiteConnection) {
            // Create preferences table
            connection.execSQL("""
                CREATE TABLE IF NOT EXISTS `preferences` (
                    `key` TEXT NOT NULL,
                    `value` TEXT NOT NULL,
                    PRIMARY KEY(`key`)
                )
            """.trimIndent())

            // Ensure PortfolioCoinEntity exists (recovery migration for edge cases)
            // This is idempotent - won't fail if table already exists
            connection.execSQL("""
                CREATE TABLE IF NOT EXISTS `PortfolioCoinEntity` (
                    `coinId` TEXT NOT NULL,
                    `name` TEXT NOT NULL,
                    `symbol` TEXT NOT NULL,
                    `iconUrl` TEXT NOT NULL,
                    `averagePurchasePrice` REAL NOT NULL,
                    `amountOwned` REAL NOT NULL,
                    `timestamp` INTEGER NOT NULL,
                    PRIMARY KEY(`coinId`)
                )
            """.trimIndent())
        }
    }

    /**
     * All available migrations in order.
     * Add new migrations to this array as the database schema evolves.
     */
    val ALL_MIGRATIONS = arrayOf(
        MIGRATION_5_6,
        MIGRATION_6_7
    )
}

