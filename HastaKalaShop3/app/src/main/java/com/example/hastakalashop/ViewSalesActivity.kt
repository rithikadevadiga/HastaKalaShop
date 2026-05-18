package com.example.hastakalashop

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hastakalashop.data.AppDatabase
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class ViewSalesActivity : AppCompatActivity() {

    private lateinit var inventoryAdapter: InventoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_sales)

        // 1. Setup Toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val salesText = findViewById<TextView>(R.id.salesText)
        val rvInventory = findViewById<RecyclerView>(R.id.rvInventory)
        val pieChart = findViewById<PieChart>(R.id.pieChart)
        val bestSellerText = findViewById<TextView>(R.id.bestSeller)
        val incomeText = findViewById<TextView>(R.id.totalIncome)

        // 2. Setup Inventory RecyclerView
        inventoryAdapter = InventoryAdapter(emptyList())
        rvInventory.layoutManager = LinearLayoutManager(this)
        rvInventory.adapter = inventoryAdapter

        // 3. Bottom Navigation Logic (4 Tabs)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.selectedItemId = R.id.nav_analytics
        
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_collection -> {
                    startActivity(Intent(this, MyCraftCollectionActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_analytics -> true // Already here
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }

        // Determine if Dark Mode is active to set chart text colors
        val isDarkMode = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        val textColor = if (isDarkMode) Color.WHITE else Color.BLACK

        val db = AppDatabase.getDatabase(this)

        lifecycleScope.launch {
            // 1. -------- DISPLAY INVENTORY (RecyclerView) --------
            val inventoryItems = db.inventoryDao().getAllInventory()
            inventoryAdapter.updateData(inventoryItems)

            // 2. -------- DISPLAY SALES HISTORY & PROCESS ANALYTICS --------
            val salesList = db.salesDao().getAllSales()
            val salesDisplay = StringBuilder()
            val colorMap = HashMap<String, Int>()
            var totalIncome = 0

            salesList.forEach { sale ->
                salesDisplay.append("${sale.productName} - ${sale.color} - ₹${sale.price}\n")
                
                // Process for analytics
                colorMap[sale.color] = colorMap.getOrDefault(sale.color, 0) + 1
                totalIncome += sale.price
            }
            salesText.text = if (salesList.isEmpty()) "No sales recorded yet." else salesDisplay.toString().trim()

            // 3. -------- BEST SELLER & REVENUE --------
            var maxFreq = 0
            val bestSellersList = mutableListOf<String>()

            for ((key, value) in colorMap) {
                if (value > maxFreq) {
                    maxFreq = value
                    bestSellersList.clear()
                    bestSellersList.add(key)
                } else if (value == maxFreq && maxFreq > 0) {
                    bestSellersList.add(key)
                }
            }

            val bestSellerDisplay = if (bestSellersList.isEmpty()) getString(R.string.no_best_seller) else bestSellersList.joinToString(", ")
            bestSellerText.text = getString(R.string.best_seller_prefix, bestSellerDisplay)
            incomeText.text = getString(R.string.total_revenue, totalIncome.toString())

            // 4. -------- PIE CHART --------
            if (colorMap.isNotEmpty()) {
                val entries = ArrayList<PieEntry>()
                for ((key, value) in colorMap) {
                    entries.add(PieEntry(value.toFloat(), key))
                }

                val dataSet = PieDataSet(entries, "")
                dataSet.colors = ColorTemplate.VORDIPLOM_COLORS.toList()
                dataSet.sliceSpace = 3f
                dataSet.valueTextColor = Color.WHITE
                dataSet.valueTextSize = 12f

                val pieData = PieData(dataSet)
                pieChart.data = pieData
                pieChart.description.isEnabled = false
                pieChart.isDrawHoleEnabled = true
                pieChart.setHoleColor(Color.TRANSPARENT)
                
                pieChart.setEntryLabelColor(textColor)
                pieChart.centerText = "Colors"
                pieChart.setCenterTextColor(textColor)
                
                pieChart.legend.isEnabled = false
                pieChart.animateY(1400)
                pieChart.invalidate()
            }
        }
    }
}
