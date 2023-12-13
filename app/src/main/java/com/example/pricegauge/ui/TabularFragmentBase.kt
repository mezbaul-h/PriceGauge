package com.example.pricegauge.ui

import android.view.View
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment

open class TabularFragmentBase: Fragment() {
    fun addBottomBorder(tableLayout: TableLayout, tableRow: TableRow) {
        val borderView = View(context)
        val params = TableRow.LayoutParams(
            TableRow.LayoutParams.MATCH_PARENT,
            1 // Height of the border
        )
        params.setMargins(0, 0, 0, 0) // Adjust margins as needed

        borderView.layoutParams = params
        borderView.setBackgroundColor(resources.getColor(android.R.color.darker_gray)) // Set border color

        // Add the border view below the TableRow
        tableLayout.addView(borderView)
    }

    fun addTopBorder(tableLayout: TableLayout, tableRow: TableRow) {
        val borderView = View(context)
        val params = TableRow.LayoutParams(
            TableRow.LayoutParams.MATCH_PARENT,
            1 // Height of the border
        )
        params.setMargins(0, 0, 0, 0) // Adjust margins as needed

        borderView.layoutParams = params
        borderView.setBackgroundColor(resources.getColor(android.R.color.darker_gray)) // Set border color

        // Add the border view above the TableRow
        tableLayout.addView(borderView, tableLayout.indexOfChild(tableRow))
    }

    fun createTextView(
        text: String,
        gravity: Int? = null,
        weight: Float? = null,
        typeface: Int? = null
    ): TextView {
        val textView = TextView(requireContext())
        //val layoutParams: LinearLayout.LayoutParams = textView.layoutParams as LinearLayout.LayoutParams

        textView.text = text
        textView.setPadding(8, 8, 8, 8)

        if (weight != null) {
            //layoutParams.weight = weight
        }

        if (gravity != null) {
            textView.gravity = gravity
        }

        if (typeface != null) {
            textView.setTypeface(null, typeface)
        }

        return textView
    }
}
