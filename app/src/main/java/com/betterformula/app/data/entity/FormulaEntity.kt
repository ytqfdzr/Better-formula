package com.betterformula.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "formulas")
data class FormulaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val expression: String,
    val variables: String,
    val createdAt: Long,
    val updatedAt: Long,
)
