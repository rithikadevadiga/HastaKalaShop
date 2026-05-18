package com.example.hastakalashop

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.example.hastakalashop.data.AppDatabase
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // 1. Setup Toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        val switchDarkMode = findViewById<SwitchMaterial>(R.id.switchDarkMode)
        val btnResetData = findViewById<View>(R.id.btnResetData)
        val btnLogout = findViewById<MaterialButton>(R.id.btnLogout)

        // 2. Dark Mode Logic
        switchDarkMode.isChecked = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        val db = AppDatabase.getDatabase(this)

        // 3. Clear Data Logic
        btnResetData.setOnClickListener {
            showResetConfirmationDialog(db)
        }

        // 4. Logout Logic
        btnLogout.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // 5. Bottom Navigation Integration
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.selectedItemId = R.id.nav_settings
        
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_analytics -> {
                    startActivity(Intent(this, ViewSalesActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_collection -> {
                    startActivity(Intent(this, MyCraftCollectionActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_settings -> true // Already here
                else -> false
            }
        }
    }

    private fun showResetConfirmationDialog(db: AppDatabase) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.confirm_clear_title))
            .setMessage(getString(R.string.confirm_clear_message))
            .setPositiveButton("Delete Everything") { _, _ ->
                lifecycleScope.launch {
                    db.inventoryDao().clearInventory()
                    db.salesDao().clearSales()
                    db.craftDao().clearAllCrafts()
                    Toast.makeText(this@SettingsActivity, getString(R.string.clear_data_success), Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
