package com.example.madecie3

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class SignupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val name = findViewById<EditText>(R.id.name)
        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)
        val confirmPassword = findViewById<EditText>(R.id.confirmPassword)
        val registerBtn = findViewById<Button>(R.id.registerBtn)

        registerBtn.setOnClickListener {

            val nameText = name.text.toString().trim()
            val emailText = email.text.toString().trim()
            val passText = password.text.toString().trim()
            val confirmText = confirmPassword.text.toString().trim()

            // 1. Name validation
            if (nameText.isEmpty()) {
                name.error = "Enter your name"
                name.requestFocus()
                return@setOnClickListener
            }

            if (nameText.length < 3) {
                name.error = "Name must be at least 3 characters"
                name.requestFocus()
                return@setOnClickListener
            }

            // 2. Email / Phone validation
            if (emailText.isEmpty()) {
                email.error = "Enter email or phone"
                email.requestFocus()
                return@setOnClickListener
            }

            val isValidEmail = Patterns.EMAIL_ADDRESS.matcher(emailText).matches()
            val isValidPhone = Patterns.PHONE.matcher(emailText).matches()

            if (!isValidEmail && !isValidPhone) {
                email.error = "Enter valid email or phone"
                email.requestFocus()
                return@setOnClickListener
            }

            // 3. Password validation
            if (passText.isEmpty()) {
                password.error = "Enter password"
                password.requestFocus()
                return@setOnClickListener
            }

            if (passText.length < 4) {
                password.error = "Password must be at least 4 characters"
                password.requestFocus()
                return@setOnClickListener
            }

            // 4. Confirm password
            if (confirmText.isEmpty()) {
                confirmPassword.error = "Confirm your password"
                confirmPassword.requestFocus()
                return@setOnClickListener
            }

            if (passText != confirmText) {
                confirmPassword.error = "Passwords do not match"
                confirmPassword.requestFocus()
                return@setOnClickListener
            }

            // ✅ SUCCESS
            Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()

            // Navigate back to login
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}