package com.example.madecie3

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.madecie3.api.RetrofitClient
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val nameText    = findViewById<TextView>(R.id.profileName)
        val emailText   = findViewById<TextView>(R.id.profileEmail)
        val logoutBtn   = findViewById<Button>(R.id.logoutBtn)
        val progressBar = findViewById<ProgressBar>(R.id.profileProgress)

        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                // Fetch user 1 as demo — in a real app you'd use the saved user ID
                val response = RetrofitClient.api.getUser(1)
                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!
                    nameText.text  = "👤 ${user.username}"
                    emailText.text = "📧 ${user.email}"
                } else {
                    val prefs = getSharedPreferences("prefs", MODE_PRIVATE)
                    nameText.text  = "👤 ${prefs.getString("username", "User")}"
                    emailText.text = "📧 –"
                }
            } catch (e: Exception) {
                nameText.text  = "👤 Profile"
                emailText.text = "Network error: ${e.message}"
            } finally {
                progressBar.visibility = View.GONE
            }
        }

        logoutBtn.setOnClickListener {
            getSharedPreferences("prefs", MODE_PRIVATE).edit().clear().apply()
            startActivity(Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        }
    }
}