package com.example.hastakalashop

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Setup Toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // 2. Dashboard Quick Action Cards (ONLY 2)
        findViewById<MaterialCardView>(R.id.cardAddSale).setOnClickListener {
            startActivity(Intent(this, AddSaleActivity::class.java))
        }
        findViewById<MaterialCardView>(R.id.cardAddInventory).setOnClickListener {
            startActivity(Intent(this, AddInventoryActivity::class.java))
        }

        // 3. Bottom Navigation Integration (Reorganized to 4 Tabs)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.selectedItemId = R.id.nav_home
        
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true 
                R.id.nav_collection -> {
                    startActivity(Intent(this, MyCraftCollectionActivity::class.java))
                    true
                }
                R.id.nav_analytics -> {
                    startActivity(Intent(this, ViewSalesActivity::class.java))
                    true
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
}
