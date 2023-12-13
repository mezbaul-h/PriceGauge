package com.example.pricegauge

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.Month
import java.util.Currency
import java.util.Date
import java.util.Locale

class PGUtils {
    companion object {
        fun calculatePercentageChange(oldValue: Double, newValue: Double): Double {
            return ((newValue - oldValue) / oldValue) * 100
        }

        fun convertDateToString(date: Date, format: String): String {
            val dateFormat = SimpleDateFormat(format, Locale.getDefault())
            return dateFormat.format(date)
        }

        fun convertStringToDate(dateString: String, format: String): Date? {
            val dateFormat = SimpleDateFormat(format, Locale.getDefault())
            return try {
                dateFormat.parse(dateString)
            } catch (e: Exception) {
                // Handle the exception if the parsing fails
                null
            }
        }

        fun getCurrentDateString(format: String): String {
            return convertDateToString(Date(), format)
        }

        fun getFormattedAmount(amount: Double, currencyCode: String = "GBP"): String {
            val currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
            val currency = Currency.getInstance(currencyCode)
            currencyFormat.currency = currency

            return currencyFormat.format(amount)
        }

        fun getFormattedRate(rate: Double): String {
            return String.format("%.2f%%", rate)
        }

        fun getMonthNameFromIndex(index: Int): String {
            return Month.values()[index].toString().lowercase().capitalize()
        }
    }
}