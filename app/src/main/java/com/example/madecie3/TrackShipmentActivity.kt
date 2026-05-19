package com.example.madecie3

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.madecie3.data.FirestoreShipmentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TrackShipmentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeUtils.applyTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_shipment)
        val currentUser = AuthGuard.requireAuthenticated(this) ?: return

        val input       = findViewById<EditText>(R.id.trackingInput)
        val btn         = findViewById<Button>(R.id.trackBtn)
        val statusText  = findViewById<TextView>(R.id.statusText)
        val progressBar = findViewById<ProgressBar>(R.id.trackProgress)
        val resultCard  = findViewById<LinearLayout>(R.id.trackResultCard)

        // SharedPreferences Use #3: Pre-fill the last searched tracking ID
        val prefs = getSharedPreferences("prefs", MODE_PRIVATE)
        val lastId = prefs.getString("last_tracking_id", "")
        if (!lastId.isNullOrEmpty()) {
            input.setText(lastId)
        }

        btn.setOnClickListener {
            val trackingId = input.text.toString().trim().uppercase()
            if (trackingId.isEmpty()) {
                Toast.makeText(this, "Enter a shipment tracking ID", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            progressBar.visibility = View.VISIBLE
            resultCard.visibility = View.GONE
            statusText.text = ""

            lifecycleScope.launch {
                try {
                    val result = withContext(Dispatchers.IO) {
                        FirestoreShipmentRepository().findShipmentByTrackingId(
                            uid = currentUser.uid,
                            trackingId = trackingId
                        )
                    }
                    val shipment = result.getOrElse { error ->
                        statusText.text = "Unable to fetch shipment: ${error.localizedMessage}"
                        resultCard.visibility = View.VISIBLE
                        return@launch
                    }

                    if (shipment != null) {
                        // SharedPreferences Use #3: Save this successful search for next time
                        prefs.edit().putString("last_tracking_id", trackingId).apply()

                        val createdOn = shipment.createdAt?.toDate()?.toString() ?: "Pending sync timestamp"

                        statusText.text = """
                            Shipment ID: ${shipment.trackingId}
                            Sender: ${shipment.sender}
                            Receiver: ${shipment.receiver}
                            Weight: ${shipment.weight} KG
                            Payment: ${shipment.paymentMethod}
                            Amount: ₹${shipment.cost}
                            Status: ${shipment.status}
                            Pickup: ${shipment.pickupAddress}
                            Delivery: ${shipment.deliveryAddress}
                            Created: $createdOn
                        """.trimIndent()
                        resultCard.visibility = View.VISIBLE
                    } else {
                        statusText.text = "No shipment found for tracking ID $trackingId."
                        resultCard.visibility = View.VISIBLE
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