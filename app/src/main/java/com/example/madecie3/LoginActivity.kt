package com.example.madecie3

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)
        val loginBtn = findViewById<Button>(R.id.loginBtn)
        val signupText = findViewById<TextView>(R.id.signupText)

        // LOGIN BUTTON CLICK
        loginBtn.setOnClickListener {

            val emailText = email.text.toString().trim()
            val passText = password.text.toString().trim()

            // 1. Empty check
            if (emailText.isEmpty()) {
                email.error = "Enter email or phone"
                email.requestFocus()
                return@setOnClickListener
            }

            if (passText.isEmpty()) {
                password.error = "Enter password"
                password.requestFocus()
                return@setOnClickListener
            }

            // 2. Email or Phone validation
            val isValidEmail = Patterns.EMAIL_ADDRESS.matcher(emailText).matches()
            val isValidPhone = Patterns.PHONE.matcher(emailText).matches()

            if (!isValidEmail && !isValidPhone) {
                email.error = "Enter valid email or phone"
                email.requestFocus()
                return@setOnClickListener
            }

            // 3. Password length (basic rule)
            if (passText.length < 4) {
                password.error = "Password must be at least 4 characters"
                password.requestFocus()
                return@setOnClickListener
            }

            // ✅ SUCCESS (for now no backend)
            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()

            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }

        // SIGNUP CLICK
        signupText.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }
}