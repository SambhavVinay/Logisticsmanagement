package com.example.madecie3

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.madecie3.data.FirestoreShipmentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ShipmentsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeUtils.applyTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shipments)
        val currentUser = AuthGuard.requireAuthenticated(this) ?: return

        val recyclerView = findViewById<RecyclerView>(R.id.shipmentsRecyclerView)
        val countText    = findViewById<TextView>(R.id.shipmentCountText)

        recyclerView.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            val shipments = withContext(Dispatchers.IO) {
                FirestoreShipmentRepository().getShipments(currentUser.uid)
            }.getOrElse {
                countText.text = "0 Total"
                recyclerView.adapter = ShipmentAdapter(emptyList())
                return@launch
            }

            countText.text = "${shipments.size} Total"
            recyclerView.adapter = ShipmentAdapter(shipments)
        }
    }
}
