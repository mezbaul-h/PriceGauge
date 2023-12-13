package com.example.pricegauge

import java.util.Calendar

object PGSettings {
    var personalExpensesMonth = Calendar.getInstance().get(Calendar.MONTH)
    var personalExpensesYear = Calendar.getInstance().get(Calendar.YEAR)
    var personalInflationFromYear = Calendar.getInstance().get(Calendar.YEAR) - 1
    var personalInflationToYear = Calendar.getInstance().get(Calendar.YEAR)
}
