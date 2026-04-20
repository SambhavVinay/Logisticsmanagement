package com.example.madecie3

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.madecie3.api.RetrofitClient
import kotlinx.coroutines.launch

class CouriersActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_couriers)

        val first = findViewById<TextView>(R.id.courierOne)
        val second = findViewById<TextView>(R.id.courierTwo)
        val third = findViewById<TextView>(R.id.courierThree)

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getUsers()
                if (response.isSuccessful && response.body() != null) {
                    val users = response.body()!!
                    val lines = users.map {
                        val name = it.username.ifBlank { "Courier" }
                        "$name\nEmail: ${it.email}"
                    }
                    if (lines.isNotEmpty()) first.text = lines[0]
                    if (lines.size > 1) second.text = lines[1]
                    if (lines.size > 2) third.text = lines[2]
                }
            } catch (_: Exception) {
                first.text = "Unable to load carrier list from ShipEngine."
            }
        }
    }
}
