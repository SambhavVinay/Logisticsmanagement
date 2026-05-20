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

class PartnerShipmentsActivity : AppCompatActivity() {

    private lateinit var countText: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var repository: FirestoreShipmentRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeUtils.applyTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_partner_shipments)
        
        // No AuthGuard for custom roles unless configured, but we can assume we are logged in.
        val currentUser = AuthGuard.requireAuthenticated(this) ?: return
        
        repository = FirestoreShipmentRepository()
        recyclerView = findViewById(R.id.shipmentsRecyclerView)
        countText = findViewById(R.id.shipmentCountText)

        recyclerView.layoutManager = LinearLayoutManager(this)

        loadShipments()
    }

    private fun loadShipments() {
        lifecycleScope.launch {
            val shipments = withContext(Dispatchers.IO) {
                repository.getAllShipments()
            }.getOrElse {
                countText.text = "0 Total"
                recyclerView.adapter = PartnerShipmentAdapter(emptyList()) { trackingId, status -> 
                    updateStatus(trackingId, status)
                }
                return@launch
            }

            countText.text = "${shipments.size} Total"
            recyclerView.adapter = PartnerShipmentAdapter(shipments) { trackingId, status ->
                updateStatus(trackingId, status)
            }
        }
    }

    private fun updateStatus(trackingId: String, newStatus: String) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                repository.updateShipmentStatus(trackingId, newStatus)
            }
            // Reload list after updating
            loadShipments()
        }
    }
}
