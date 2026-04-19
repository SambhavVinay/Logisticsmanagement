package com.example.madecie3



import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class TrackShipmentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_shipment)

        val input = findViewById<EditText>(R.id.trackingInput)
        val btn = findViewById<Button>(R.id.trackBtn)
        val status = findViewById<TextView>(R.id.statusText)

        btn.setOnClickListener {
            val id = input.text.toString()

            if (id.isEmpty()) {
                Toast.makeText(this, "Enter tracking ID", Toast.LENGTH_SHORT).show()
            } else {
                // Dummy tracking logic
                status.text = "Status: In Transit\nLocation: Bangalore\nETA: 2 days"
            }
        }
    }
}