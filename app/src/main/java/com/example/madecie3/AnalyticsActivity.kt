package com.example.madecie3

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.madecie3.api.RetrofitClient
import kotlinx.coroutines.launch

class AnalyticsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analytics)

        val delivered = findViewById<TextView>(R.id.analyticsDelivered)
        val rto = findViewById<TextView>(R.id.analyticsRto)
        val ndr = findViewById<TextView>(R.id.analyticsNdr)

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getCarts()
                if (response.isSuccessful && response.body() != null) {
                    val carts = response.body()!!
                    val total = carts.size
                    val cancelled = carts.count { it.products.isEmpty() }
                    delivered.text = total.toString()
                    rto.text = if (total > 0) "${(cancelled * 100f / total).toInt()}%" else "0%"
                    ndr.text = "Processed: $total\nCancelled: $cancelled\nActive: ${total - cancelled}"
                }
            } catch (_: Exception) {
                ndr.text = "Unable to fetch analytics from ShipEngine right now."
            }
        }
    }
}
