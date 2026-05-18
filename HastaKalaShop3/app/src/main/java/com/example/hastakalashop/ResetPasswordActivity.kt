package com.example.hastakalashop

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.hastakalashop.data.AppDatabase
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class ResetPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        // Find views from the layout
        val etNewPassword = findViewById<TextInputEditText>(R.id.etNewPassword)
        val etConfirmPassword = findViewById<TextInputEditText>(R.id.etConfirmPassword)
        val btnUpdatePassword = findViewById<MaterialButton>(R.id.btnUpdatePassword)

        // Get the username passed from ForgotPasswordActivity
        val username = intent.getStringExtra("USERNAME") ?: ""
        val db = AppDatabase.getDatabase(this)

        btnUpdatePassword.setOnClickListener {
            val newPassword = etNewPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            // 1. Basic validation: check if fields are empty
            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, getString(R.string.fill_fields_error), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 2. Check if passwords match
            if (newPassword != confirmPassword) {
                Toast.makeText(this, getString(R.string.password_mismatch), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 3. Update password in the Room Database
            lifecycleScope.launch {
                val user = db.userDao().getUserByUsername(username)
                if (user != null) {
                    // Create a copy of the user with the new password
                    val updatedUser = user.copy(password = newPassword)
                    db.userDao().updateUser(updatedUser)
                    
                    // Show success message
                    Toast.makeText(this@ResetPasswordActivity, getString(R.string.password_update_success), Toast.LENGTH_SHORT).show()
                    
                    // Navigate back to Login screen and clear the task stack
                    val intent = Intent(this@ResetPasswordActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@ResetPasswordActivity, "Error: User not found", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
