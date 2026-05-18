package com.example.hastakalashop

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hastakalashop.data.AppDatabase
import com.example.hastakalashop.data.Craft
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import kotlinx.coroutines.launch

class MyCraftCollectionActivity : AppCompatActivity() {

    private lateinit var adapter: CraftAdapter
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_craft_collection)

        // 1. Setup Toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        db = AppDatabase.getDatabase(this)
        
        val rvCrafts = findViewById<RecyclerView>(R.id.rvCrafts)
        val fabAdd = findViewById<ExtendedFloatingActionButton>(R.id.fabAddCraft)

        // 2. Setup RecyclerView with 2 columns
        rvCrafts.layoutManager = GridLayoutManager(this, 2)
        adapter = CraftAdapter(emptyList()) { craft ->
            showDeleteConfirmation(craft)
        }
        rvCrafts.adapter = adapter

        fabAdd.setOnClickListener {
            startActivity(Intent(this, AddCraftActivity::class.java))
        }

        // 3. Bottom Navigation Integration
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.selectedItemId = R.id.nav_collection
        
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
                R.id.nav_collection -> true // Already here
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }

        loadCrafts()
    }

    override fun onResume() {
        super.onResume()
        loadCrafts()
    }

    private fun showDeleteConfirmation(craft: Craft) {
        AlertDialog.Builder(this)
            .setTitle("Delete Craft")
            .setMessage("Are you sure you want to delete this craft?")
            .setPositiveButton("Delete") { _, _ ->
                deleteCraft(craft)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteCraft(craft: Craft) {
        lifecycleScope.launch {
            db.craftDao().deleteCraft(craft)
            loadCrafts()
        }
    }

    private fun loadCrafts() {
        lifecycleScope.launch {
            val crafts = db.craftDao().getAllCrafts()
            adapter.updateList(crafts)
        }
    }
}
