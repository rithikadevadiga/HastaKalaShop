package com.example.hastakalashop.data

import androidx.room.*

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    @Query("SELECT * FROM users WHERE username = :username AND phoneNumber = :phone AND favoriteCraftType = :favCraft LIMIT 1")
    suspend fun verifyUser(username: String, phone: String, favCraft: String): User?

    @Update
    suspend fun updateUser(user: User)
}

@Dao
interface InventoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: InventoryItem)

    @Query("SELECT * FROM inventory")
    suspend fun getAllInventory(): List<InventoryItem>

    @Query("SELECT * FROM inventory WHERE LOWER(productName) = :name AND LOWER(color) = :color LIMIT 1")
    suspend fun getItem(name: String, color: String): InventoryItem?

    @Update
    suspend fun updateItem(item: InventoryItem)

    @Query("DELETE FROM inventory")
    suspend fun clearInventory()
}

@Dao
interface SalesDao {
    @Insert
    suspend fun insertSale(sale: Sale)

    @Query("SELECT * FROM sales ORDER BY date DESC")
    suspend fun getAllSales(): List<Sale>

    @Query("DELETE FROM sales")
    suspend fun clearSales()
}

@Dao
interface CraftDao {
    @Insert
    suspend fun insertCraft(craft: Craft)

    @Query("SELECT * FROM craft_collection ORDER BY id DESC")
    suspend fun getAllCrafts(): List<Craft>

    @Delete
    suspend fun deleteCraft(craft: Craft)

    @Query("DELETE FROM craft_collection")
    suspend fun clearAllCrafts()
}
