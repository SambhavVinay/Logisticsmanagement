package com.example.madecie3

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.madecie3.api.Product
import com.example.madecie3.api.RetrofitClient
import kotlinx.coroutines.launch

class OrdersActivity : AppCompatActivity() {

    private lateinit var adapter: InventoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeUtils.applyTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)

        val recycler = findViewById<RecyclerView>(R.id.recyclerView)
        val progressBar = findViewById<ProgressBar>(R.id.ordersProgress)
        val errorText = findViewById<TextView>(R.id.ordersError)
        val searchInput = findViewById<EditText>(R.id.inventorySearchInput)

        recycler.layoutManager = LinearLayoutManager(this)
        adapter = InventoryAdapter(emptyList(), this)
        recycler.adapter = adapter
        progressBar.visibility = View.VISIBLE

        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getProducts()
                if (response.isSuccessful && response.body() != null) {
                    val products = response.body()!!
                    adapter.updateData(products)
                    errorText.visibility = View.GONE
                } else {
                    errorText.text = "Failed to load inventory."
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