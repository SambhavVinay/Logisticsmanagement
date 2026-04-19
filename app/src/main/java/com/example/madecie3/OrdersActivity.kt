package com.example.madecie3

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.madecie3.api.Cart
import com.example.madecie3.api.RetrofitClient
import kotlinx.coroutines.launch

class OrdersActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)

        val recycler    = findViewById<RecyclerView>(R.id.recyclerView)
        val progressBar = findViewById<ProgressBar>(R.id.ordersProgress)
        val errorText   = findViewById<TextView>(R.id.ordersError)

        recycler.layoutManager = LinearLayoutManager(this)
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getCarts()
                if (response.isSuccessful && response.body() != null) {
                    val carts = response.body()!!
                    recycler.adapter = CartAdapter(carts, this@OrdersActivity)
                    errorText.visibility = View.GONE
                } else {
                    errorText.text = "Failed to load orders."
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