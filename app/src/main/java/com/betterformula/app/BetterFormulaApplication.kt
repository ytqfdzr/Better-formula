package com.betterformula.app

import android.app.Application
import com.betterformula.app.data.AppContainer

class BetterFormulaApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
