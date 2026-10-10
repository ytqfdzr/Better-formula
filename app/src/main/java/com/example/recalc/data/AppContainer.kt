package com.example.recalc.data

import android.content.Context
import com.example.recalc.data.db.AppDatabase

class AppContainer(context: Context) {

    val formulaRepository: FormulaRepository

    init {
        val database = AppDatabase.getInstance(context)
        formulaRepository = FormulaRepository(database)
    }
}
