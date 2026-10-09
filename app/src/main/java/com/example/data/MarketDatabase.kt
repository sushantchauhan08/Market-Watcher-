package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PriceAlert::class, FavoriteItem::class], version = 1, exportSchema = false)
abstract class MarketDatabase : RoomDatabase() {
    abstract fun marketDao(): MarketDao

    companion object {
        @Volatile
        private var INSTANCE: MarketDatabase? = null

        fun getDatabase(context: Context): MarketDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MarketDatabase::class.java,
                    "market_watcher_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
