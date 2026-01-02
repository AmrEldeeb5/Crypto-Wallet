package com.example.valguard.app.portfolio.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class UserBalanceEntity(
    @PrimaryKey val id: String,
    val cashBalance: Double,
)
