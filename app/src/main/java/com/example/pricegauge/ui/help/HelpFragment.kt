package com.example.pricegauge.ui.help

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import com.example.pricegauge.R
import com.example.pricegauge.databinding.FragmentHelpBinding


class HelpFragment : Fragment() {

    private var _binding: FragmentHelpBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHelpBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val webView: WebView = root.findViewById(R.id.fragment_help_web_view)

        webView.setBackgroundColor(Color.TRANSPARENT)
        webView.settings.javaScriptEnabled = true

        // Load the HTML content from the raw resource
        val htmlString =
            resources.openRawResource(R.raw.help).bufferedReader().use { it.readText() }
        webView.loadDataWithBaseURL(null, htmlString, "text/html", "UTF-8", null)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}