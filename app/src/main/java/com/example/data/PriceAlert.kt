package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "price_alerts")
data class PriceAlert(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val itemName: String,
    val targetPrice: Double,
    val comparisonType: String = "LESS_THAN", // "LESS_THAN" or "GREATER_THAN"
    val isActive: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)
