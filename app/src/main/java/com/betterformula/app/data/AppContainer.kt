package com.betterformula.app.data

import android.content.Context
import com.betterformula.app.data.db.AppDatabase

class AppContainer(context: Context) {

    val formulaRepository: FormulaRepository
    val settingsRepository: SettingsRepository

    init {
        val database = AppDatabase.getInstance(context)
        formulaRepository = FormulaRepository(database)
        settingsRepository = SettingsRepository(context)
    }
}
