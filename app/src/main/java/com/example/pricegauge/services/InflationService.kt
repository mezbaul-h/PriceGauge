package com.example.pricegauge.services

import ExpenseRepository
import com.example.pricegauge.PGConstants
import com.example.pricegauge.PGUtils
import java.util.Calendar
import java.util.Date

data class InflationRecord (
    val month: Int,
    val year: Int,
    val totalExpense: Double,
    val inflationRateToPreviousMonth: Double,
    val inflationRateToAllPreviousMonths: Double
)

class InflationService (private val expenseRepository: ExpenseRepository) {
    suspend fun getInflationRecords(fromYear: Int, toYear: Int): List<InflationRecord> {
        val records = mutableListOf<InflationRecord>()
        var totalExpense: Double = 0.0
        var previousMonthExpense: Double = 0.0
        var previousAllMonthsExpense: Double = 0.0
        var previousAllMonthsCount: Int = 0

        for (year in fromYear..toYear) {
            for (month in 0..11) {
                val expenses = expenseRepository.getMonthExpenses(month, year)

                // Calculate total expense for the month
                totalExpense = expenses.sumOf { it.amount }

                // Calculate inflation rates
                var inflationRateToPreviousMonth = PGUtils.calculatePercentageChange(previousMonthExpense, totalExpense)

                if (inflationRateToPreviousMonth.isNaN() || inflationRateToPreviousMonth.isInfinite()) {
                    inflationRateToPreviousMonth = 0.0
                }

                val previousAllMonthsExpenseAvg = previousAllMonthsExpense / previousAllMonthsCount
                var inflationRateToAllPreviousMonths = PGUtils.calculatePercentageChange(previousAllMonthsExpenseAvg, totalExpense)

                if (inflationRateToAllPreviousMonths.isNaN() || inflationRateToAllPreviousMonths.isInfinite()) {
                    inflationRateToAllPreviousMonths = 0.0
                }

                // Add record to the result
                records.add(InflationRecord(month, year, totalExpense, inflationRateToPreviousMonth, inflationRateToAllPreviousMonths))

                // Update previous month expenses for the next iteration
                previousMonthExpense = totalExpense
                previousAllMonthsCount += 1
                previousAllMonthsExpense += totalExpense
            }
        }

        return records
    }

    fun getPersonalInflationRateComparedToLastMonth(): Double {
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val lastMonth = (currentMonth - 1 + 12) % 12
        var lastMonthsYear = currentYear

        if (currentMonth == 0) {
            lastMonthsYear -= 1
        }

        val previousGrossExpense = expenseRepository.getMonthExpenses(lastMonth, lastMonthsYear).sumOf { it.amount }
        val currentGrossExpense = expenseRepository.getMonthExpenses(currentMonth, currentYear).sumOf { it.amount }

        var inflationRate = PGUtils.calculatePercentageChange(previousGrossExpense, currentGrossExpense)

        if (inflationRate.isNaN() || inflationRate.isInfinite()) {
            inflationRate = 0.0
        }

        return inflationRate
    }

    fun getPersonalInflationRateComparedToOverallAndYearRange(): Triple<Double, Int, Int> {
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        val previousExpenses = expenseRepository.getExpensesBeforeMonthYear(currentMonth, currentYear)

        var firstYear = currentYear

        if (previousExpenses.isNotEmpty()) {
            println(previousExpenses[0].date)
            firstYear = previousExpenses[0].date.substring(0, 4).toInt(10)
        }

        val previousGrossExpense = previousExpenses.sumOf { it.amount }
        val previousAvgExpense = previousGrossExpense / previousExpenses.size
        val currentGrossExpense = expenseRepository.getMonthExpenses(currentMonth, currentYear).sumOf { it.amount }

        var inflationRate = PGUtils.calculatePercentageChange(previousAvgExpense, currentGrossExpense)

        if (inflationRate.isNaN() || inflationRate.isInfinite()) {
            inflationRate = 0.0
        }

        return Triple(inflationRate, firstYear, currentYear)
    }
}