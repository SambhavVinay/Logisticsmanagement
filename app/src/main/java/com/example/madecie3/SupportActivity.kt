package com.example.madecie3

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.madecie3.api.RetrofitClient
import kotlinx.coroutines.launch

class SupportActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_support)

        val cardOne = findViewById<TextView>(R.id.supportCardOne)
        val cardTwo = findViewById<TextView>(R.id.supportCardTwo)

        lifecycleScope.launch {
            try {
                val users = RetrofitClient.api.getUsers().body().orEmpty()
                val products = RetrofitClient.api.getProducts().body().orEmpty()
                cardOne.text = "Accounts discovered: ${users.size}\nAPI source: /users"
                cardTwo.text = "Products available: ${products.size}\nAPI source: /products"
            } catch (_: Exception) {
                cardOne.text = "Support data unavailable right now."
            }
        }
    }
}
