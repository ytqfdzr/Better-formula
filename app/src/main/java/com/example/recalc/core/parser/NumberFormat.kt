package com.example.recalc.core.parser

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Locale
import kotlin.math.abs

object NumberFormat {

    private const val LARGE_THRESHOLD = 1e9
    private const val SMALL_THRESHOLD = 1e-5

    fun format(value: Double): String {
        if (value == 0.0) return "0"
        val magnitude = abs(value)
        return if (magnitude >= LARGE_THRESHOLD || magnitude < SMALL_THRESHOLD) {
            String.format(Locale.ROOT, "%.5e", value)
        } else {
            BigDecimal.valueOf(value)
                .setScale(5, RoundingMode.HALF_UP)
                .stripTrailingZeros()
                .toPlainString()
        }
    }
}
