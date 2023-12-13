package com.example.pricegauge.ui.expenses

import ExpenseRepository
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.example.pricegauge.PGConstants
import com.example.pricegauge.PGUtils
import com.example.pricegauge.R
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date


class AddExpenseDialogFragment : DialogFragment() {

    private val expenseRepository by lazy { ExpenseRepository(requireContext()) }

    // Interface to communicate with the parent fragment
    interface OnExpenseAddedListener {
        fun onExpenseAdded()
    }

    private var listener: OnExpenseAddedListener? = null

    // Function to set the listener
    fun setOnExpenseAddedListener(listener: OnExpenseAddedListener) {
        this.listener = listener
    }

    // Function to call the listener
    private fun notifyExpenseAdded() {
        listener?.onExpenseAdded()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater

        val view = inflater.inflate(R.layout.add_expense_dialog_layout, null)
        val addExpenseDateEditText: EditText =
            view.findViewById<EditText>(R.id.add_expense_date_edit_text)

        addExpenseDateEditText.setText(PGUtils.getCurrentDateString(PGConstants.DATE_PICKER_DATE_FORMAT))
        addExpenseDateEditText.setOnClickListener(View.OnClickListener {
            showDatePicker(it)
        })

        builder.setView(view)
            .setTitle("New Expense")
            .setCancelable(true)
            .setPositiveButton("Add") { _, _ ->
                // Handle OK button click
                addExpenseEntry(view)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                // Handle Cancel button click
                dialog.dismiss()
            }

        return builder.create()
    }

    private fun addExpenseEntry(view: View) {
        val addExpenseAmountEditText: EditText =
            view.findViewById(R.id.add_expense_amount_edit_text)
        val addExpenseDateEditText: EditText =
            view.findViewById(R.id.add_expense_date_edit_text)
        val addExpenseNoteEditText: EditText =
            view.findViewById(R.id.add_expense_note_edit_text)
        val addExpenseTitleEditText: EditText =
            view.findViewById(R.id.add_expense_title_edit_text)

        val amount = addExpenseAmountEditText.text.toString().toDouble()
        val date = PGUtils.convertStringToDate(
            addExpenseDateEditText.text.toString(),
            PGConstants.DATE_PICKER_DATE_FORMAT
        )
        val note = addExpenseNoteEditText.text.toString()
        val title = addExpenseTitleEditText.text.toString()

        if (date != null) {
            lifecycleScope.launch {
                expenseRepository.createExpense(amount, title, note, date)

                // Notify the parent fragment about the added expense
                notifyExpenseAdded()
            }
        }
    }

    private fun showDatePicker(view: View) {
        val calendar = Calendar.getInstance()

        val datePickerDialog = DatePickerDialog(
            view.context,
            DatePickerDialog.OnDateSetListener { _: DatePicker?, year: Int, month: Int, day: Int ->
                // handle date selection
                calendar.set(year, month, day)
                val selectedDate: Date = calendar.time
                (view as EditText).setText(
                    PGUtils.convertDateToString(
                        selectedDate,
                        PGConstants.DATE_PICKER_DATE_FORMAT
                    )
                )
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }
}
