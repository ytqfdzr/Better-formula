package com.example.recalc.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.recalc.data.entity.HistoryRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    @Query("SELECT * FROM history ORDER BY timestamp DESC LIMIT :limit")
    fun observeRecent(limit: Int): Flow<List<HistoryRecordEntity>>

    @Query("SELECT id FROM history ORDER BY timestamp ASC")
    suspend fun getIdsOrderedByTimestampAsc(): List<Long>

    @Insert
    suspend fun insert(record: HistoryRecordEntity): Long

    @Query("DELETE FROM history WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<Long>)

    @Query("DELETE FROM history")
    suspend fun clear()
}
