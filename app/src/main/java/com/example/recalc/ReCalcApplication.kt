package com.example.recalc

import android.app.Application
import com.example.recalc.data.AppContainer

class ReCalcApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
