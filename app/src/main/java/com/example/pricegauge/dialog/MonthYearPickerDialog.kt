package com.example.pricegauge.dialog

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.NumberPicker
import com.example.pricegauge.R
import java.util.Calendar

class MonthYearPickerDialog(
    context: Context,
    private val listener: DatePickerDialog.OnDateSetListener,
    private val initialYear: Int,
    private val initialMonth: Int
) : DatePickerDialog(context, listener, initialYear, initialMonth, 1) {

    private lateinit var yearPicker: NumberPicker
    private lateinit var monthPicker: NumberPicker

    init {
        setTitle("Select Month and Year")
        setButton(BUTTON_POSITIVE, "Set") { _, _ ->
            listener.onDateSet(
                null,
                yearPicker.value,
                monthPicker.value,
                1
            )
        }

        setButton(BUTTON_NEGATIVE, "Cancel") { dialog, _ ->
            dialog.cancel()
        }

        val cal = Calendar.getInstance()
        val currentYear = cal.get(Calendar.YEAR)

        val view = layoutInflater.inflate(R.layout.dialog_month_year_picker, null)
        setView(view)

        // Initialize month and year pickers
        monthPicker = view.findViewById(R.id.dialog_month_year_picker_month_picker)
        yearPicker = view.findViewById(R.id.dialog_month_year_picker_year_picker)

        monthPicker.minValue = 0
        monthPicker.maxValue = 11
        monthPicker.displayedValues = arrayOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )
        monthPicker.value = initialMonth

        yearPicker.minValue = currentYear - 50
        yearPicker.maxValue = currentYear + 50
        yearPicker.value = initialYear
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        datePicker.findViewById<NumberPicker>(context.resources.getIdentifier("day", "id", "android"))?.visibility =
            NumberPicker.GONE
    }
}
