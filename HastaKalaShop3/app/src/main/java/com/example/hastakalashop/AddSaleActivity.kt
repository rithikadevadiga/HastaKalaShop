package com.example.hastakalashop

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.hastakalashop.data.AppDatabase
import com.example.hastakalashop.data.InventoryItem
import com.example.hastakalashop.data.Sale
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class AddSaleActivity : AppCompatActivity() {

    private lateinit var actvInventory: MaterialAutoCompleteTextView
    private lateinit var etPrice: TextInputEditText
    private var selectedItem: InventoryItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_sale)

        // 1. Setup Toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        // 2. UI Elements
        actvInventory = findViewById(R.id.actvInventory)
        etPrice = findViewById(R.id.price)
        val btnSave = findViewById<MaterialButton>(R.id.saveBtn)

        val db = AppDatabase.getDatabase(this)

        // 3. Load Available Inventory into Dropdown
        loadInventory(db)

        // 4. Handle Selection
        actvInventory.setOnItemClickListener { parent, _, position, _ ->
            // Use getItemAtPosition to get the actual data object
            val item = parent.getItemAtPosition(position) as InventoryItem
            selectedItem = item
            
            // Auto-fill the price from inventory database
            etPrice.setText(item.price.toString())
        }

        // 5. Save Sale Logic
        btnSave.setOnClickListener {
            val priceStr = etPrice.text.toString().trim()

            if (selectedItem == null) {
                Toast.makeText(this, getString(R.string.select_product_hint), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (priceStr.isEmpty()) {
                Toast.makeText(this, getString(R.string.fill_fields_error), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val price = priceStr.toIntOrNull() ?: 0

            lifecycleScope.launch {
                selectedItem?.let { item ->
                    // Re-fetch the item from DB to ensure freshest stock count
                    val currentDbItem = db.inventoryDao().getItem(item.productName.lowercase(), item.color.lowercase())
                    
                    if (currentDbItem != null && currentDbItem.remainingStock > 0) {
                        // a) Reduce stock: Increase Sold count, Decrease Remaining count
                        currentDbItem.stockSold += 1
                        currentDbItem.remainingStock -= 1
                        db.inventoryDao().updateItem(currentDbItem)

                        // b) Record the transaction in Sales table
                        val saleRecord = Sale(
                            productName = currentDbItem.productName,
                            color = currentDbItem.color,
                            price = price
                        )
                        db.salesDao().insertSale(saleRecord)

                        Toast.makeText(this@AddSaleActivity, getString(R.string.sale_saved_success), Toast.LENGTH_SHORT).show()

                        // c) Automatic Low Stock Warning
                        if (currentDbItem.remainingStock <= 2 && currentDbItem.remainingStock > 0) {
                            Toast.makeText(this@AddSaleActivity, getString(R.string.low_stock_warning, currentDbItem.remainingStock, currentDbItem.productName), Toast.LENGTH_LONG).show()
                        } else if (currentDbItem.remainingStock == 0) {
                            Toast.makeText(this@AddSaleActivity, "⚠ ${currentDbItem.productName} is now OUT OF STOCK!", Toast.LENGTH_LONG).show()
                        }
                        
                        finish() // Return to dashboard
                    } else {
                        Toast.makeText(this@AddSaleActivity, getString(R.string.out_of_stock_error, item.productName), Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun loadInventory(db: AppDatabase) {
        lifecycleScope.launch {
            // Fetch items that have at least 1 in stock
            val allItems = db.inventoryDao().getAllInventory()
            val availableItems = allItems.filter { it.remainingStock > 0 }

            if (availableItems.isEmpty()) {
                actvInventory.setHint(getString(R.string.no_items_in_inventory))
                actvInventory.isEnabled = false
            } else {
                // Custom adapter to display Product (Color) - Stock: X
                val adapter = object : ArrayAdapter<InventoryItem>(
                    this@AddSaleActivity,
                    android.R.layout.simple_dropdown_item_1line,
                    availableItems
                ) {
                    override fun getView(position: Int, convertView: View?, parent: android.view.ViewGroup): View {
                        val view = super.getView(position, convertView, parent)
                        val text = view.findViewById<android.widget.TextView>(android.R.id.text1)
                        val item = getItem(position)
                        text.text = "${item?.productName} (${item?.color}) — Stock: ${item?.remainingStock}"
                        return view
                    }
                }
                actvInventory.setAdapter(adapter)
                actvInventory.isEnabled = true
            }
        }
    }
}
