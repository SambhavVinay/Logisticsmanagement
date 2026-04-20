package com.example.madecie3

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.madecie3.api.RetrofitClient
import kotlinx.coroutines.launch

class RatesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rates)

        val content = findViewById<TextView>(R.id.ratesContent)
        val progress = findViewById<ProgressBar>(R.id.ratesProgress)
        progress.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getProducts()
                if (response.isSuccessful && response.body() != null) {
                    val products = response.body()!!
                    val avgPrice = if (products.isNotEmpty()) {
                        products.map { it.price }.average()
                    } else {
                        0.0
                    }
                    content.text = "Sample rates loaded.\nItems: ${products.size}\nAverage: $${"%.2f".format(avgPrice)}"
                } else {
                    content.text = "Rate estimate failed.\nUnable to fetch sample pricing data."
                }
            } catch (e: Exception) {
                content.text = "Network error: ${e.message}"
            } finally {
                progress.visibility = View.GONE
            }
        }
    }
}
