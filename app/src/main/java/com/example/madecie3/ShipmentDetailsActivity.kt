package com.example.madecie3

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ShipmentDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shipment_details)

        val id = intent.getStringExtra("id")
        val text = findViewById<TextView>(R.id.detailText)

        text.text = """
            Tracking ID: $id
            Status: In Transit
            From: Bangalore
            To: Chennai
            Payment: Paid
        """.trimIndent()
    }
}