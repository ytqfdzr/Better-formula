package com.betterformula.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.betterformula.app.data.entity.FormulaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FormulaDao {

    @Query("SELECT * FROM formulas ORDER BY updatedAt DESC")
    fun observeAll(): Flow<List<FormulaEntity>>

    @Query("SELECT * FROM formulas ORDER BY updatedAt DESC")
    suspend fun getAll(): List<FormulaEntity>

    @Query("SELECT * FROM formulas WHERE id = :id")
    suspend fun getById(id: Long): FormulaEntity?

    @Query("SELECT COUNT(*) FROM formulas")
    suspend fun count(): Int

    @Insert
    suspend fun insert(formula: FormulaEntity): Long

    @Update
    suspend fun update(formula: FormulaEntity)

    @Query("DELETE FROM formulas WHERE id = :id")
    suspend fun deleteById(id: Long)
}
