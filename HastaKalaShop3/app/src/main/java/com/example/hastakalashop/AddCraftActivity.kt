package com.example.hastakalashop

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.hastakalashop.data.AppDatabase
import com.example.hastakalashop.data.Craft
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class AddCraftActivity : AppCompatActivity() {

    private var selectedImageUri: Uri? = null
    private lateinit var ivCraftImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_craft)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        ivCraftImage = findViewById(R.id.ivCraftImage)
        val btnSelectImage = findViewById<MaterialButton>(R.id.btnSelectImage)
        val etCraftName = findViewById<TextInputEditText>(R.id.etCraftName)
        val etCategory = findViewById<TextInputEditText>(R.id.etCategory)
        val etPrice = findViewById<TextInputEditText>(R.id.etPrice)
        val etNotes = findViewById<TextInputEditText>(R.id.etNotes)
        val btnSaveCraft = findViewById<MaterialButton>(R.id.btnSaveCraft)

        val db = AppDatabase.getDatabase(this)

        val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                selectedImageUri = it
                ivCraftImage.setImageURI(it)
                // Persist permissions for the URI if it's a content URI
                contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        }

        btnSelectImage.setOnClickListener {
            pickImage.launch("image/*")
        }

        btnSaveCraft.setOnClickListener {
            val name = etCraftName.text.toString().trim()
            val category = etCategory.text.toString().trim()
            val priceStr = etPrice.text.toString().trim()
            val notes = etNotes.text.toString().trim()

            if (name.isEmpty() || category.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val price = priceStr.toDoubleOrNull() ?: 0.0
            val imageUriString = selectedImageUri?.toString() ?: ""

            lifecycleScope.launch {
                val newCraft = Craft(
                    name = name,
                    category = category,
                    notes = notes,
                    price = price,
                    imageUri = imageUriString
                )
                db.craftDao().insertCraft(newCraft)
                Toast.makeText(this@AddCraftActivity, "Craft added to collection!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
