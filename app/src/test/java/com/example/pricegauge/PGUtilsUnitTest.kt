package com.example.pricegauge

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class PGUtilsUnitTest {
    @Test
    fun formattedAmount_isCorrect() {
        assertEquals(PGUtils.getFormattedAmount(30.588, currencyCode = "GBP"), "£30.59")
        assertEquals(PGUtils.getFormattedAmount(30.5, currencyCode = "USD"), "$30.50")
    }

    @Test
    fun percentageChange_isCorrect() {
        val actual = 100.0
        assertEquals(PGUtils.calculatePercentageChange(50.0, 100.0).toString(), actual.toString())
    }
}