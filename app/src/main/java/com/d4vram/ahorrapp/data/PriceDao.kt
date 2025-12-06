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

    @Query("SELECT DISTINCT productName FROM price_entries WHERE productName LIKE '%' || :query || '%' AND productName IS NOT NULL ORDER BY productName ASC LIMIT 20")
    fun searchProductNames(query: String): Flow<List<String>>

    @Query("SELECT * FROM price_entries WHERE productName = :productName ORDER BY price ASC")
    fun getPricesForProduct(productName: String): Flow<List<PriceEntryEntity>>
    @Query("SELECT DISTINCT productName FROM price_entries WHERE productName IS NOT NULL ORDER BY productName ASC")
    fun getAllProductNames(): Flow<List<String>>

    @Query("SELECT * FROM price_entries WHERE productName IS NOT NULL")
    fun getAllPrices(): Flow<List<PriceEntryEntity>>
}
