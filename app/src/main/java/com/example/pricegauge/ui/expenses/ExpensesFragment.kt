package com.example.pricegauge.ui.expenses

import ExpenseRepository
import android.app.DatePickerDialog
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.pricegauge.PGConstants
import com.example.pricegauge.PGSettings
import com.example.pricegauge.PGUtils
import com.example.pricegauge.R
import com.example.pricegauge.databinding.FragmentExpensesBinding
import com.example.pricegauge.datastore.entity.Expense
import com.example.pricegauge.dialog.MonthYearPickerDialog
import com.example.pricegauge.ui.TabularFragmentBase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date


class ExpensesFragment : TabularFragmentBase(), AddExpenseDialogFragment.OnExpenseAddedListener {

    private var _binding: FragmentExpensesBinding? = null

    private val expenseRepository by lazy { ExpenseRepository(requireContext()) }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExpensesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.addExpenseFab.setOnClickListener { _ ->
            showTextEntryDialog()
        }

        binding.expensesMonthPickerEditText.setOnClickListener { view ->
            showMonthPicker(view)
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateUI(view)
    }

    // Method to show the month picker
    fun showMonthPicker(view: View) {
        val initialYear = PGSettings.personalExpensesYear
        val initialMonth = PGSettings.personalExpensesMonth

        val dialog = MonthYearPickerDialog(
            view.context,
            DatePickerDialog.OnDateSetListener { _, year, month, _ ->
                PGSettings.personalExpensesMonth = month
                PGSettings.personalExpensesYear = year

                updateUI(view)
            },
            initialYear,
            initialMonth
        )
        dialog.show()
    }

    private fun showTextEntryDialog() {
        val dialog = AddExpenseDialogFragment()

        // Set the listener to the parent fragment
        dialog.setOnExpenseAddedListener(this)

        dialog.show(childFragmentManager, "AddExpenseDialogFragment")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun populateTableLayout(expenses: List<Expense>) {
        val tableLayout: TableLayout = requireView().findViewById(R.id.expenses_table_layout)

        // Remove existing rows if any
        tableLayout.removeAllViews()

        // Create header row
        val headerRow = TableRow(requireContext())

        headerRow.addView(createTextView("Date", typeface = Typeface.BOLD))
        headerRow.addView(createTextView("Title", weight = 1.0f, typeface = Typeface.BOLD))
        headerRow.addView(createTextView("Amount", gravity = Gravity.END, typeface = Typeface.BOLD))

        tableLayout.addView(headerRow)

        addBottomBorder(tableLayout, headerRow)

        var totalMonthlyExpense = 0.0

        // Populate rows with data
        for (expense in expenses) {
            val row = TableRow(requireContext())

            row.addView(
                createTextView(
                    PGUtils.convertDateToString(
                        PGUtils.convertStringToDate(
                            expense.date,
                            PGConstants.SQLITE_DATETIME_FORMAT
                        ) as Date,
                        "dd-MM-yyyy"
                    )
                )
            )
            row.addView(createTextView(expense.title, weight = 1.0f))
            row.addView(createTextView(PGUtils.getFormattedAmount(expense.amount), gravity = Gravity.END))

            tableLayout.addView(row)

            totalMonthlyExpense += expense.amount
        }

        // If no records found, render a placeholder row.
        if (expenses.isEmpty()) {
            val row = TableRow(requireContext())

            row.addView(createTextView("--"))
            row.addView(createTextView("--", weight = 1.0f))
            row.addView(createTextView("--", gravity = Gravity.END))

            tableLayout.addView(row)
        }

        val footerRow = TableRow(requireContext())

        footerRow.addView(createTextView(""))
        footerRow.addView(
            createTextView(
                "Total Expense",
                gravity = Gravity.END,
                weight = 1.0f,
                typeface = Typeface.BOLD
            )
        )
        footerRow.addView(
            createTextView(
                PGUtils.getFormattedAmount(totalMonthlyExpense),
                gravity = Gravity.END,
                typeface = Typeface.BOLD
            )
        )

        tableLayout.addView(footerRow)

        addTopBorder(tableLayout, footerRow)
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun updateUI(view: View) {
        val editText = view.findViewById<EditText>(R.id.expenses_month_picker_edit_text)

        editText.setText(
            getString(
                R.string.placeholder_comma_separated_with_space,
                PGUtils.getMonthNameFromIndex(PGSettings.personalExpensesMonth),
                PGSettings.personalExpensesYear.toString()
            )
        )

        // Launch a coroutine for background work
        GlobalScope.launch(Dispatchers.IO) {
            // Fetch data from the background thread
            val monthlyExpenses = expenseRepository.getMonthExpenses(
                PGSettings.personalExpensesMonth,
                PGSettings.personalExpensesYear
            )

            // Switch to the main thread to update the UI
            withContext(Dispatchers.Main) {
                // Update UI on the main thread
                populateTableLayout(monthlyExpenses)
            }
        }
    }

    override fun onExpenseAdded() {
        updateUI(this.requireView())
    }
}