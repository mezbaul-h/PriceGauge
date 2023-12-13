package com.example.pricegauge.ui.inflation

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
import androidx.fragment.app.Fragment
import com.example.pricegauge.PGConstants
import com.example.pricegauge.PGSettings
import com.example.pricegauge.PGUtils
import com.example.pricegauge.R
import com.example.pricegauge.databinding.FragmentInflationPersonalBinding
import com.example.pricegauge.dialog.YearPickerDialog
import com.example.pricegauge.services.InflationRecord
import com.example.pricegauge.services.InflationService
import com.example.pricegauge.ui.TabularFragmentBase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import kotlin.math.exp

class PersonalInflationFragment : TabularFragmentBase() {

    private var _binding: FragmentInflationPersonalBinding? = null

    private val expenseRepository by lazy { ExpenseRepository(requireContext()) }

    private val inflationService: InflationService by lazy {
        InflationService(expenseRepository)
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInflationPersonalBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.personalInflationFromYearPickerEditText.setOnClickListener { view ->
            showYearPicker(view, true)
        }

        binding.personalInflationToYearPickerEditText.setOnClickListener { view ->
            showYearPicker(view, false)
        }

        return root
    }

    // Method to show the year picker
    fun showYearPicker(view: View, from: Boolean) {
        var initialYear = PGSettings.personalInflationFromYear

        if (!from) {
            initialYear = PGSettings.personalInflationToYear
        }

        val dialog = YearPickerDialog(
            view.context,
            DatePickerDialog.OnDateSetListener { _, year, _, _ ->
                if (from) {
                    PGSettings.personalInflationFromYear = year
                } else {
                    PGSettings.personalInflationToYear = year
                }

                updateUI(view)
            },
            initialYear,
        )
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateUI(view)
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun updateUI(view: View) {
        view.findViewById<EditText>(R.id.personal_inflation_from_year_picker_edit_text)?.setText(
            getString(
                R.string.placeholder_single_value,
                PGSettings.personalInflationFromYear.toString()
            )
        )

        view.findViewById<EditText>(R.id.personal_inflation_to_year_picker_edit_text)?.setText(
            getString(
                R.string.placeholder_single_value,
                PGSettings.personalInflationToYear.toString()
            )
        )

        GlobalScope.launch(Dispatchers.IO) {
            // Fetch data from the background thread
            val inflationRecords = inflationService.getInflationRecords(
                PGSettings.personalInflationFromYear, PGSettings.personalInflationToYear
            )

            // Switch to the main thread to update the UI
            withContext(Dispatchers.Main) {
                // Update UI on the main thread
                populateTableLayout(inflationRecords)
            }
        }
    }

    private fun populateTableLayout(inflationRecords: List<InflationRecord>) {
        val tableLayout: TableLayout = requireView().findViewById(R.id.personal_inflation_table_layout)

        // Remove existing rows if any
        tableLayout.removeAllViews()

        // Create header row
        val headerRow = TableRow(requireContext())

        headerRow.addView(createTextView("Month", typeface = Typeface.BOLD))
        headerRow.addView(createTextView("Monthly Rate", weight = 1.0f, typeface = Typeface.BOLD))
        headerRow.addView(createTextView("Overall Rate", gravity = Gravity.END, typeface = Typeface.BOLD))
        headerRow.addView(createTextView("Total Expense", gravity = Gravity.END, typeface = Typeface.BOLD))

        tableLayout.addView(headerRow)

        addBottomBorder(tableLayout, headerRow)

        var grossExpense = 0.0

        // Populate rows with data
        for (item in inflationRecords) {
            val row = TableRow(requireContext())

            row.addView(
                createTextView(
                    getString(R.string.placeholder_space_separated, PGUtils.getMonthNameFromIndex(item.month), item.year.toString())
                )
            )
            row.addView(createTextView(PGUtils.getFormattedRate(item.inflationRateToPreviousMonth), weight = 1.0f))
            row.addView(createTextView(PGUtils.getFormattedRate(item.inflationRateToAllPreviousMonths), gravity = Gravity.END))
            row.addView(createTextView(PGUtils.getFormattedAmount(item.totalExpense), gravity = Gravity.END))

            tableLayout.addView(row)

            grossExpense += item.totalExpense
        }

        // If no records found, render a placeholder row.
        if (inflationRecords.isEmpty()) {
            val row = TableRow(requireContext())

            row.addView(createTextView("--"))
            row.addView(createTextView("--", gravity = Gravity.END, weight = 1.0f))
            row.addView(createTextView("--", gravity = Gravity.END))
            row.addView(createTextView("--", gravity = Gravity.END))

            tableLayout.addView(row)
        }

        val footerRow = TableRow(requireContext())

        footerRow.addView(createTextView(""))
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
                PGUtils.getFormattedAmount(grossExpense),
                gravity = Gravity.END,
                typeface = Typeface.BOLD
            )
        )

        tableLayout.addView(footerRow)

        addTopBorder(tableLayout, footerRow)
    }
}