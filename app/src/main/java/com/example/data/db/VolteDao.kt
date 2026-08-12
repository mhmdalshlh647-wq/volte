package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface VolteDao {
    @Query("SELECT deviceId FROM favorite_devices ORDER BY addedAt DESC")
    fun getFavoriteDeviceIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(fav: FavoriteDeviceEntity)

    @Query("DELETE FROM favorite_devices WHERE deviceId = :deviceId")
    suspend fun removeFavorite(deviceId: String)

    @Query("SELECT * FROM search_history ORDER BY timestamp DESC LIMIT 20")
    fun getSearchHistory(): Flow<List<SearchHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSearchHistory(history: SearchHistoryEntity)

    @Query("DELETE FROM search_history WHERE `query` = :query")
    suspend fun deleteSearchQuery(query: String)

    @Query("DELETE FROM search_history")
    suspend fun clearSearchHistory()

    @Query("DELETE FROM favorite_devices")
    suspend fun clearFavorites()
}
