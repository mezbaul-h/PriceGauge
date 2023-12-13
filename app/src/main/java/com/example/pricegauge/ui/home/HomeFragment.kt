package com.example.pricegauge.ui.home

import ExpenseRepository
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.pricegauge.PGSettings
import com.example.pricegauge.PGUtils
import com.example.pricegauge.R
import com.example.pricegauge.databinding.FragmentHomeBinding
import com.example.pricegauge.services.InflationService
import java.util.Calendar


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

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
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateUI(view)
    }

    private fun updateUI(view: View) {
        val (inflationRate, firstYear, currentYear) = inflationService.getPersonalInflationRateComparedToOverallAndYearRange()

        var overallDateRange = getString(
            R.string.placeholder_dash_separated,
            firstYear.toString(),
            currentYear.toString()
        )

        if (firstYear == currentYear) {
            overallDateRange = getString(
                R.string.placeholder_single_value,
                firstYear.toString(),
            )
        }

        view.findViewById<TextView>(R.id.home_overall_personal_text_view)?.text =
            getString(
                R.string.placeholder_space_separated,
                "Personal",
                PGUtils.getFormattedRate(inflationRate)
            )

        view.findViewById<TextView>(R.id.home_overall_date_range_text_view)?.text = overallDateRange

        view.findViewById<TextView>(R.id.home_last_month_personal_text_view)?.text =
            getString(
                R.string.placeholder_space_separated,
                "Personal",
                PGUtils.getFormattedRate(inflationService.getPersonalInflationRateComparedToLastMonth())
            )

        view.findViewById<TextView>(R.id.home_last_month_date_range_text_view)?.text =
            getString(
                R.string.placeholder_single_value,
                PGUtils.getCurrentDateString("MMM yyyy")
            )
    }
}