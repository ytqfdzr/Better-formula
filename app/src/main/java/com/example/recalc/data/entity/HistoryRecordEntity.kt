package com.example.recalc.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class HistoryRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val formulaName: String,
    val expression: String,
    val inputsJson: String,
    val result: String,
    val timestamp: Long,
)
