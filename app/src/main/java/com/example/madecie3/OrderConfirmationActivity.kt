package com.example.madecie3

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class OrderConfirmationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_confirmation)

        val trackingId = intent.getStringExtra("trackingId")
        val trackingText = findViewById<TextView>(R.id.trackingText)
        val homeBtn = findViewById<Button>(R.id.homeBtn)

        trackingText.text = "Tracking ID: $trackingId"

        homeBtn.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
        }
    }
}