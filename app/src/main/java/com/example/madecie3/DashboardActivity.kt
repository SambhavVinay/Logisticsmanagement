package com.example.madecie3

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val createBtn = findViewById<TextView>(R.id.createShipmentBtn)
        val trackBtn = findViewById<TextView>(R.id.trackBtn)
        val ordersBtn = findViewById<TextView>(R.id.ordersBtn)
        val profileBtn = findViewById<TextView>(R.id.profileBtn)

        createBtn.setOnClickListener {
            startActivity(Intent(this, CreateShipmentActivity::class.java))
        }

        trackBtn.setOnClickListener {
            startActivity(Intent(this, TrackShipmentActivity::class.java))
        }

        ordersBtn.setOnClickListener {
            startActivity(Intent(this, OrdersActivity::class.java))
        }

        profileBtn.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}