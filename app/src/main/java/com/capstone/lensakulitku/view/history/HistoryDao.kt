package com.capstone.lensakulitku.view.history

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: HistoryEntity)

    @Query("SELECT * FROM history")
    suspend fun getAllHistory(): List<HistoryEntity>

    @Query("SELECT * FROM history WHERE id = :id")
    suspend fun getHistoryById(id: Int): HistoryEntity?

    @Query("SELECT * FROM history ORDER BY id DESC LIMIT 1")
    suspend fun getLatestHistory(): HistoryEntity?

    // Tambahkan metode untuk memperbarui baselineImageUri
    @Query("UPDATE history SET baselineImageUri = :baselineImageUri WHERE id = :historyId")
    suspend fun updateBaselineImageUri(historyId: Int, baselineImageUri: String)
}

