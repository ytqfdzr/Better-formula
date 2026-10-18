package com.betterformula.app.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * 应用设置持久化(SharedPreferences 封装,不引入额外依赖)。
 * 目前仅承载"是否在主界面显示最近计算历史"一个开关。
 */
class SettingsRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /** 是否显示最近计算历史;默认 (DEFAULT_SHOW_RECENT_HISTORY)。 */
    val showRecentHistory: Flow<Boolean> = callbackFlow {
        trySend(readShowRecentHistory())
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == KEY_SHOW_RECENT_HISTORY) {
                trySend(readShowRecentHistory())
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    fun setShowRecentHistory(show: Boolean) {
        prefs.edit().putBoolean(KEY_SHOW_RECENT_HISTORY, show).apply()
    }

    private fun readShowRecentHistory(): Boolean =
        prefs.getBoolean(KEY_SHOW_RECENT_HISTORY, DEFAULT_SHOW_RECENT_HISTORY)

    companion object {
        const val DEFAULT_SHOW_RECENT_HISTORY = true

        private const val PREFS_NAME = "better_formula_settings"
        private const val KEY_SHOW_RECENT_HISTORY = "show_recent_history"
    }
}