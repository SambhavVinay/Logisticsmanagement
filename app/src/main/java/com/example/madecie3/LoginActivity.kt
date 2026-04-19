package com.example.madecie3

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.madecie3.api.LoginRequest
import com.example.madecie3.api.RetrofitClient
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val email       = findViewById<EditText>(R.id.email)
        val password    = findViewById<EditText>(R.id.password)
        val loginBtn    = findViewById<Button>(R.id.loginBtn)
        val signupText  = findViewById<TextView>(R.id.signupText)
        val progressBar = findViewById<ProgressBar>(R.id.loginProgress)

        loginBtn.setOnClickListener {
            val username = email.text.toString().trim()
            val pass     = password.text.toString().trim()

            if (username.isEmpty()) { email.error = "Enter username or email"; return@setOnClickListener }
            if (pass.isEmpty())     { password.error = "Enter password"; return@setOnClickListener }

            progressBar.visibility = View.VISIBLE
            loginBtn.isEnabled = false

            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.api.login(LoginRequest(username, pass))
                    if (response.isSuccessful && response.body() != null) {
                        val token = response.body()!!.token
                        // Save token in SharedPreferences
                        getSharedPreferences("prefs", MODE_PRIVATE).edit()
                            .putString("token", token)
                            .putString("username", username)
                            .apply()
                        Toast.makeText(this@LoginActivity, "Login Successful!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Invalid credentials", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@LoginActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
                } finally {
                    progressBar.visibility = View.GONE
                    loginBtn.isEnabled = true
                }
            }
        }

        signupText.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }
}