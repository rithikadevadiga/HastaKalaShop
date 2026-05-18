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

class ForgotPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        val etUsername = findViewById<TextInputEditText>(R.id.etForgotUsername)
        val etPhone = findViewById<TextInputEditText>(R.id.etForgotPhone)
        val etFavCraft = findViewById<TextInputEditText>(R.id.etForgotFavCraft)
        val btnVerify = findViewById<MaterialButton>(R.id.btnVerify)

        val db = AppDatabase.getDatabase(this)

        btnVerify.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val favCraft = etFavCraft.text.toString().trim()

            if (username.isEmpty() || phone.isEmpty() || favCraft.isEmpty()) {
                Toast.makeText(this, getString(R.string.fill_fields_error), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val user = db.userDao().verifyUser(username, phone, favCraft)
                if (user != null) {
                    val intent = Intent(this@ForgotPasswordActivity, ResetPasswordActivity::class.java)
                    intent.putExtra("USERNAME", username)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@ForgotPasswordActivity, getString(R.string.verification_failed), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
