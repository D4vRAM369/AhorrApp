package com.d4vram.ahorrapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PriceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: PriceEntryEntity)

    @Query("SELECT * FROM price_entries ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<PriceEntryEntity>>

    @Query("DELETE FROM price_entries WHERE id = :id")
    suspend fun delete(id: Long)

    @androidx.room.Update
    suspend fun update(entry: PriceEntryEntity)
}
