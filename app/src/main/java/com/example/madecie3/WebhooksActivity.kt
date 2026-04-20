package com.example.madecie3

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.madecie3.api.RetrofitClient
import kotlinx.coroutines.launch

class WebhooksActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webhooks)

        val content = findViewById<TextView>(R.id.webhooksContent)
        val progress = findViewById<ProgressBar>(R.id.webhooksProgress)
        progress.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getUsers()
                if (response.isSuccessful && response.body() != null) {
                    val users = response.body()!!
                    val count = users.size
                    content.text = "Total configured hooks (sample): $count\nSource route: /users"
                } else {
                    content.text = "Failed to fetch webhooks."
                }
            } catch (e: Exception) {
                content.text = "Network error: ${e.message}"
            } finally {
                progress.visibility = View.GONE
            }
        }
    }
}
