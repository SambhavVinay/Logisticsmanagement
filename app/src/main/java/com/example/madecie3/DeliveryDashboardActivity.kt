package com.example.madecie3

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.madecie3.api.RetrofitClient
import kotlinx.coroutines.launch
import java.util.Calendar

class DeliveryDashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeUtils.applyTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delivery_dashboard)

        val ordersBtn    = findViewById<TextView>(R.id.ordersBtn)
        val profileBtn   = findViewById<LinearLayout>(R.id.profileBtn)
        val greeting    = findViewById<TextView>(R.id.dashGreeting)
        val themeToggleBtn = findViewById<ImageButton>(R.id.themeToggleBtn)

        // Greeting based on time of day
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val timeLabel = when {
            hour < 12 -> "GOOD MORNING"
            hour < 17 -> "GOOD AFTERNOON"
            else -> "GOOD EVENING"
        }
        greeting.text = timeLabel

        // Theme toggle
        themeToggleBtn.setOnClickListener {
            ThemeUtils.toggleTheme(this)
            recreate()
        }

        ordersBtn.setOnClickListener    { startActivity(Intent(this, PartnerShipmentsActivity::class.java)) }
        profileBtn.setOnClickListener   { startActivity(Intent(this, ProfileActivity::class.java)) }
    }
}