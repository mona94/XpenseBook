package com.money.xpensesbookproject.ui.dashboard

import android.graphics.Color
import android.icu.number.NumberFormatter
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.money.xpensesbookproject.R
import com.money.xpensesbookproject.data.model.DashboardState
import com.money.xpensesbookproject.data.model.ExpenseCategoryData
import com.money.xpensesbookproject.databinding.FragmentDashboardBinding
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = requireNotNull(_binding) { "Binding is null!" }


    private val viewModel: DashboardViewModel by viewModels()
    private val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpPieChart()
        observeDashboardState()
    }

    private fun observeDashboardState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.dashboardState.collect { state: DashboardState ->
                updateDashboardUI(state)
            }
        }
    }

    private fun updateDashboardUI(state: DashboardState) {
        binding.apply {
            Log.d("Dashboard", "updateDashboardUI: $state")
            txtBalance.text = currencyFormatter.format(state.balance)
            txtTotalIncome.text = currencyFormatter.format(state.totalIncome)
            txtTotalExpenses.text = currencyFormatter.format(state.totalExpenses)

            updatePieChart(state.expensesCategory)
        }
    }

    private fun updatePieChart(categories: List<ExpenseCategoryData>) {
        if (categories.isEmpty()) return

        val entries = categories.map { category ->
            PieEntry(category.amount.toFloat(), category.category)
        }

        val colors = listOf(
            Color.rgb(244, 67, 54),    // Red
            Color.rgb(33, 150, 243),   // Blue
            Color.rgb(76, 175, 80),    // Green
            Color.rgb(255, 193, 7),    // Amber/Yellow
            Color.rgb(156, 39, 176),   // Purple
            Color.rgb(255, 87, 34),    // Deep Orange
            Color.rgb(0, 188, 212),    // Cyan
            Color.rgb(121, 85, 72)     // Brown
            // Original -> Vibrant version
//            Color.rgb(
//                45,
//                85,
//                155
//            ),   // Royal blue: Increased saturation and lightness while keeping the blue dominant
//            Color.rgb(
//                130,
//                180,
//                65
//            ),   // Fresh lime green: Boosted green channel and reduced red for more punch
//            Color.rgb(
//                235,
//                125,
//                90
//            ),   // Coral: Amplified red and reduced other channels for more energy
//            Color.rgb(235, 95, 125),  // Hot pink: Increased red and added more blue for vibrancy
//            Color.rgb(190, 25, 85),     // Magenta: Boosted red while keeping blue-bias for richness
//            Color.rgb(215, 15, 95),   // Deep rose: Increased contrast between channels
//            Color.rgb(255, 95, 0),      // Pure orange: Maximized red and adjusted yellow component
//            Color.rgb(
//                255,
//                195,
//                0
//            )     // Golden yellow: Pushed both red and green channels higher  // Deep gold
        )

        val dataSet: PieDataSet = PieDataSet(entries, "").apply  {   //here in label it was written Expenses Categories which i dont want to show
            setColors(colors)
            valueFormatter = PercentFormatter(binding.pieChart)
            valueTextSize = 11f
            valueTextColor = Color.WHITE
            yValuePosition = PieDataSet.ValuePosition.INSIDE_SLICE
        }
        val pieData = PieData(dataSet)
        binding.pieChart.apply {
            data = pieData
            invalidate()
        }
    }

    private fun setUpPieChart() {
        binding.pieChart.apply {
            // General settings
            description.isEnabled = false
            setUsePercentValues(true)
            setDrawEntryLabels(false) // Show category name on slices

            // Hole (donut style)
            isDrawHoleEnabled = true
            setHoleColor(Color.TRANSPARENT) // Transparent center
            holeRadius = 55f
            transparentCircleRadius = 60f
            setTransparentCircleColor(Color.WHITE)
            setTransparentCircleAlpha(100)

            // Rotation
            rotationAngle = 0f
            isRotationEnabled = true
            isHighlightPerTapEnabled = true

            // Legend styling
            legend.apply {
                isEnabled = true
                form = Legend.LegendForm.CIRCLE
                orientation = Legend.LegendOrientation.VERTICAL
                horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
                verticalAlignment = Legend.LegendVerticalAlignment.CENTER
                setDrawInside(false)
                textSize = 12f
                xEntrySpace = 10f
                yEntrySpace = 6f
            }

            // Animate
            animateY(1200, com.github.mikephil.charting.animation.Easing.EaseInOutQuad)
        }
    }

}