package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_items")
data class FavoriteItem(
    @PrimaryKey val itemName: String,
    val isFavorite: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)
