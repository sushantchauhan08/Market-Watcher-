package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MarketDao {
    // --- Price Alerts ---
    @Query("SELECT * FROM price_alerts ORDER BY timestamp DESC")
    fun getAllAlerts(): Flow<List<PriceAlert>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: PriceAlert)

    @Update
    suspend fun updateAlert(alert: PriceAlert)

    @Delete
    suspend fun deleteAlert(alert: PriceAlert)

    @Query("DELETE FROM price_alerts WHERE id = :id")
    suspend fun deleteAlertById(id: Int)

    // --- Bookmarks / Favorites ---
    @Query("SELECT * FROM favorite_items ORDER BY timestamp DESC")
    fun getAllFavorites(): Flow<List<FavoriteItem>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_items WHERE itemName = :name AND isFavorite = 1 LIMIT 1)")
    fun isFavoriteItemFlow(name: String): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_items WHERE itemName = :name AND isFavorite = 1 LIMIT 1)")
    suspend fun isFavoriteItem(name: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteItem)

    @Delete
    suspend fun deleteFavorite(favorite: FavoriteItem)

    @Query("DELETE FROM favorite_items WHERE itemName = :itemName")
    suspend fun deleteFavoriteByName(itemName: String)
}
