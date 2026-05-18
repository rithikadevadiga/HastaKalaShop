package com.example.hastakalashop

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.hastakalashop.data.AppDatabase
import com.example.hastakalashop.data.User
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etRegUsername = findViewById<EditText>(R.id.etRegUsername)
        val etRegPhone = findViewById<EditText>(R.id.etRegPhone)
        val etRegFavCraft = findViewById<EditText>(R.id.etRegFavCraft)
        val etRegPassword = findViewById<EditText>(R.id.etRegPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val tvBackToLogin = findViewById<TextView>(R.id.tvBackToLogin)

        val db = AppDatabase.getDatabase(this)

        btnRegister.setOnClickListener {
            val username = etRegUsername.text.toString().trim()
            val phone = etRegPhone.text.toString().trim()
            val favCraft = etRegFavCraft.text.toString().trim()
            val password = etRegPassword.text.toString().trim()

            if (username.isNotEmpty() && phone.isNotEmpty() && favCraft.isNotEmpty() && password.isNotEmpty()) {
                lifecycleScope.launch {
                    val existingUser = db.userDao().getUserByUsername(username)
                    if (existingUser == null) {
                        val newUser = User(
                            username = username,
                            password = password,
                            phoneNumber = phone,
                            favoriteCraftType = favCraft
                        )
                        db.userDao().insertUser(newUser)
                        Toast.makeText(this@RegisterActivity, getString(R.string.registration_success), Toast.LENGTH_SHORT).show()
                        finish() // Go back to login
                    } else {
                        Toast.makeText(this@RegisterActivity, "Username already exists", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, getString(R.string.registration_failed), Toast.LENGTH_SHORT).show()
            }
        }

        tvBackToLogin.setOnClickListener {
            finish()
        }
    }
}
