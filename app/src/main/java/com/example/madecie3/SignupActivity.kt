package com.example.madecie3

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.madecie3.api.RetrofitClient
import kotlinx.coroutines.launch

class SignupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val name            = findViewById<EditText>(R.id.name)
        val email           = findViewById<EditText>(R.id.email)
        val password        = findViewById<EditText>(R.id.password)
        val confirmPassword = findViewById<EditText>(R.id.confirmPassword)
        val registerBtn     = findViewById<Button>(R.id.registerBtn)
        val progressBar     = findViewById<ProgressBar>(R.id.signupProgress)

        registerBtn.setOnClickListener {
            val nameText    = name.text.toString().trim()
            val emailText   = email.text.toString().trim()
            val passText    = password.text.toString().trim()
            val confirmText = confirmPassword.text.toString().trim()

            if (nameText.length < 3)        { name.error = "Name must be at least 3 characters"; return@setOnClickListener }
            if (emailText.isEmpty())        { email.error = "Enter email"; return@setOnClickListener }
            if (passText.length < 4)        { password.error = "Password must be at least 4 characters"; return@setOnClickListener }
            if (passText != confirmText)    { confirmPassword.error = "Passwords do not match"; return@setOnClickListener }

            progressBar.visibility = View.VISIBLE
            registerBtn.isEnabled = false

            lifecycleScope.launch {
                try {
                    // POST new user to FakeStore /users
                    val response = RetrofitClient.api.createUser(
                        mapOf("username" to nameText, "email" to emailText, "password" to passText)
                    )
                    if (response.isSuccessful && response.body() != null) {
                        Toast.makeText(this@SignupActivity,
                            "Account created! User ID: ${response.body()!!.id}", Toast.LENGTH_LONG).show()
                        startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@SignupActivity, "Signup failed. Try again.", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@SignupActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
                } finally {
                    progressBar.visibility = View.GONE
                    registerBtn.isEnabled = true
                }
            }
        }
    }
}