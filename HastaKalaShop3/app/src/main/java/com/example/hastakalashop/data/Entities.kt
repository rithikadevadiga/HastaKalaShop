package com.example.hastakalashop.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val password: String,
    val phoneNumber: String = "",
    val favoriteCraftType: String = ""
)

// Updated Inventory Item to track Added, Sold, and Remaining stock
@Entity(tableName = "inventory", indices = [Index(value = ["productName", "color"], unique = true)])
data class InventoryItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productName: String,
    val color: String,
    val price: Int,
    var stockAdded: Int = 0,     // Total items ever added
    var stockSold: Int = 0,      // Total items sold
    var remainingStock: Int = 0  // Items currently in stock
)

@Entity(tableName = "sales")
data class Sale(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productName: String,
    val color: String,
    val price: Int,
    val date: Long = System.currentTimeMillis()
)

@Entity(tableName = "craft_collection")
data class Craft(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val category: String,
    val notes: String,
    val price: Double,
    val imageUri: String
)
