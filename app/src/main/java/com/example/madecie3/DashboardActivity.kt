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

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val createBtn   = findViewById<TextView>(R.id.createShipmentBtn)
        val trackBtn    = findViewById<TextView>(R.id.trackBtn)
        val ordersBtn   = findViewById<TextView>(R.id.ordersBtn)
        val profileBtn  = findViewById<TextView>(R.id.profileBtn)
        val recycler    = findViewById<RecyclerView>(R.id.productsRecycler)
        val progressBar = findViewById<ProgressBar>(R.id.dashboardProgress)
        val errorText   = findViewById<TextView>(R.id.dashboardError)

        // Nav buttons
        createBtn.setOnClickListener  { startActivity(Intent(this, CreateShipmentActivity::class.java)) }
        trackBtn.setOnClickListener   { startActivity(Intent(this, TrackShipmentActivity::class.java)) }
        ordersBtn.setOnClickListener  { startActivity(Intent(this, OrdersActivity::class.java)) }
        profileBtn.setOnClickListener { startActivity(Intent(this, ProfileActivity::class.java)) }

        // Load products from FakeStore API
        recycler.layoutManager = LinearLayoutManager(this)
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
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