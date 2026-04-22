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

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeUtils.applyTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val createBtn    = findViewById<TextView>(R.id.createShipmentBtn)
        val shipmentsBtn = findViewById<TextView>(R.id.shipmentsBtn)
        val trackBtn     = findViewById<TextView>(R.id.trackBtn)
        val ordersBtn    = findViewById<TextView>(R.id.ordersBtn)
        val profileBtn   = findViewById<LinearLayout>(R.id.profileBtn)
        val recycler    = findViewById<RecyclerView>(R.id.productsRecycler)
        val progressBar = findViewById<ProgressBar>(R.id.dashboardProgress)
        val errorText   = findViewById<TextView>(R.id.dashboardError)
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

        createBtn.setOnClickListener    { startActivity(Intent(this, CreateShipmentActivity::class.java)) }
        shipmentsBtn.setOnClickListener { startActivity(Intent(this, ShipmentsActivity::class.java)) }
        trackBtn.setOnClickListener     { startActivity(Intent(this, TrackShipmentActivity::class.java)) }
        ordersBtn.setOnClickListener    { startActivity(Intent(this, OrdersActivity::class.java)) }
        profileBtn.setOnClickListener   { startActivity(Intent(this, ProfileActivity::class.java)) }
        findViewById<LinearLayout>(R.id.analyticsBtn).setOnClickListener {
            startActivity(Intent(this, AnalyticsActivity::class.java))
        }
        
        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.aiAssistantFab).setOnClickListener {
            startActivity(Intent(this, AiAssistantActivity::class.java))
        }

        // Setup Hero Card & Products Recycler
        recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            val db = com.example.madecie3.data.AppDatabase.getDatabase(this@DashboardActivity)
            val latest = db.shipmentDao().getLatestShipment()
            
            if (latest != null) {
                val heroCard = findViewById<LinearLayout>(R.id.heroShipmentCard)
                val heroId = findViewById<TextView>(R.id.heroTrackingId)
                val heroRoute = findViewById<TextView>(R.id.heroRoute)
                
                heroCard.visibility = View.VISIBLE
                heroId.text = latest.trackingId
                heroRoute.text = "${latest.sender.take(3)} -> ${latest.receiver.take(3)}".uppercase()
            }

            try {
                val response = RetrofitClient.api.getProducts()
                if (response.isSuccessful && response.body() != null) {
                    val products = response.body()!!
                    recycler.adapter = ProductAdapter(products, this@DashboardActivity)
                    errorText.visibility = View.GONE
                } else {
                    errorText.text = "Failed to load products."
                    errorText.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                errorText.text = "Network error: ${e.message}"
                errorText.visibility = View.VISIBLE
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }
}