package com.example.madecie3

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.madecie3.api.RetrofitClient
import kotlinx.coroutines.launch

class WarehousesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_warehouses)

        val content = findViewById<TextView>(R.id.warehousesContent)
        val progress = findViewById<ProgressBar>(R.id.warehousesProgress)
        progress.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getCarts()
                if (response.isSuccessful && response.body() != null) {
                    val carts = response.body()!!
                    val count = carts.size
                    val first = carts.firstOrNull()
                    content.text = """
                        Total records: $count
                        Default bucket: ${first?.id ?: "N/A"}
                        Source route: /carts
                    """.trimIndent()
                } else {
                    content.text = "Failed to fetch warehouses."
                }
            } catch (e: Exception) {
                content.text = "Network error: ${e.message}"
            } finally {
                progress.visibility = View.GONE
            }
        }
    }
}
