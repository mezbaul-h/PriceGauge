package com.example.pricegauge.dialog

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.NumberPicker
import com.example.pricegauge.R
import java.util.Calendar

class YearPickerDialog(
    context: Context,
    private val listener: DatePickerDialog.OnDateSetListener,
    private val initialYear: Int,
) : DatePickerDialog(context, listener, initialYear, 0, 1) {

    private lateinit var yearPicker: NumberPicker

    init {
        setTitle("Select Year")
        setButton(BUTTON_POSITIVE, "Set") { _, _ ->
            listener.onDateSet(
                null,
                yearPicker.value,
                0,
                1
            )
        }

        setButton(BUTTON_NEGATIVE, "Cancel") { dialog, _ ->
            dialog.cancel()
        }

        val cal = Calendar.getInstance()
        val currentYear = cal.get(Calendar.YEAR)

        val view = layoutInflater.inflate(R.layout.dialog_year_picker, null)
        setView(view)

        // Initialize year pickers
        yearPicker = view.findViewById(R.id.dialog_year_picker_year_picker)

        yearPicker.minValue = currentYear - 50
        yearPicker.maxValue = currentYear + 50
        yearPicker.value = initialYear
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        datePicker.findViewById<NumberPicker>(context.resources.getIdentifier("day", "id", "android"))?.visibility = NumberPicker.GONE
    }
}
