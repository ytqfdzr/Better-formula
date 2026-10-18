package com.betterformula.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.betterformula.app.data.dao.FormulaDao
import com.betterformula.app.data.dao.HistoryDao
import com.betterformula.app.data.entity.FormulaEntity
import com.betterformula.app.data.entity.HistoryRecordEntity

@Database(
    entities = [FormulaEntity::class, HistoryRecordEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun formulaDao(): FormulaDao
    abstract fun historyDao(): HistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "better_formula.db"
                ).build().also { INSTANCE = it }
            }
    }
}
