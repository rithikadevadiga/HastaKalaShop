package com.example.hastakalashop

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.hastakalashop.data.AppDatabase
import com.example.hastakalashop.data.InventoryItem
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class AddInventoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_inventory)

        // 1. Setup Toolbar - Back button enabled as this is a sub-page (Quick Action)
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        // 2. UI Elements Logic
        val etProduct = findViewById<TextInputEditText>(R.id.etInvProduct)
        val etColor = findViewById<TextInputEditText>(R.id.etInvColor)
        val etPrice = findViewById<TextInputEditText>(R.id.etInvPrice)
        val etStock = findViewById<TextInputEditText>(R.id.etInvStock)
        val btnSave = findViewById<MaterialButton>(R.id.btnSaveInventory)

        val db = AppDatabase.getDatabase(this)

        btnSave.setOnClickListener {
            val productName = etProduct.text.toString().trim()
            val color = etColor.text.toString().trim()
            val priceStr = etPrice.text.toString().trim()
            val stockStr = etStock.text.toString().trim()

            if (productName.isEmpty() || color.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
                Toast.makeText(this, getString(R.string.fill_fields_error), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val price = priceStr.toDoubleOrNull()?.toInt() ?: 0
            val stockInput = stockStr.toIntOrNull() ?: 0

            lifecycleScope.launch {
                val existingItem = db.inventoryDao().getItem(productName.lowercase(), color.lowercase())
                
                if (existingItem != null) {
                    existingItem.stockAdded += stockInput
                    existingItem.remainingStock += stockInput
                    val updatedItem = existingItem.copy(
                        price = price, 
                        stockAdded = existingItem.stockAdded,
                        remainingStock = existingItem.remainingStock
                    )
                    db.inventoryDao().updateItem(updatedItem)
                } else {
                    val newItem = InventoryItem(
                        productName = productName,
                        color = color,
                        price = price,
                        stockAdded = stockInput,
                        stockSold = 0,
                        remainingStock = stockInput
                    )
                    db.inventoryDao().insertItem(newItem)
                }

                Toast.makeText(this@AddInventoryActivity, getString(R.string.inventory_saved_success), Toast.LENGTH_SHORT).show()
                finish() // Close activity after saving
            }
        }
    }
}
