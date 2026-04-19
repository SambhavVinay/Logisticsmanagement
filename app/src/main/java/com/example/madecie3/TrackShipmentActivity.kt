package com.example.madecie3

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.madecie3.api.RetrofitClient
import kotlinx.coroutines.launch

class TrackShipmentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_shipment)

        val input       = findViewById<EditText>(R.id.trackingInput)
        val btn         = findViewById<Button>(R.id.trackBtn)
        val statusText  = findViewById<TextView>(R.id.statusText)
        val progressBar = findViewById<ProgressBar>(R.id.trackProgress)

        btn.setOnClickListener {
            val idText = input.text.toString().trim()
            if (idText.isEmpty()) {
                Toast.makeText(this, "Enter a Product/Shipment ID (1–20)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val id = idText.toIntOrNull()
            if (id == null || id < 1) {
                Toast.makeText(this, "Please enter a valid numeric ID", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            progressBar.visibility = View.VISIBLE
            statusText.text = ""

            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.api.getProduct(id)
                    if (response.isSuccessful && response.body() != null) {
                        val p = response.body()!!
                        statusText.text = """
                            📦 Shipment ID: ${p.id}
                            📌 Product: ${p.title}
                            💲 Value: $${"%.2f".format(p.price)}
                            📁 Category: ${p.category}
                            📍 Status: In Transit
                            🏙️ Origin: New York
                            🏙️ Destination: Los Angeles
                            ⏱️ ETA: 3 business days
                        """.trimIndent()
                    } else {
                        statusText.text = "No shipment found for ID $id.\nTry IDs between 1 and 20."
                    }
                } catch (e: Exception) {
                    statusText.text = "Network error: ${e.message}"
                } finally {
                    progressBar.visibility = View.GONE
                }
            }
        }
    }
}