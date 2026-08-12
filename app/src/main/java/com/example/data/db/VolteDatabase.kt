package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [FavoriteDeviceEntity::class, SearchHistoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class VolteDatabase : RoomDatabase() {
    abstract fun volteDao(): VolteDao

    companion object {
        @Volatile
        private var INSTANCE: VolteDatabase? = null

        fun getDatabase(context: Context): VolteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VolteDatabase::class.java,
                    "volte_technician_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
