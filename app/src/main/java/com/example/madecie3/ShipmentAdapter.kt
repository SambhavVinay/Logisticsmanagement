package com.example.madecie3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.madecie3.data.FirestoreShipment
import java.text.SimpleDateFormat
import java.util.*

class ShipmentAdapter(private val shipments: List<FirestoreShipment>) :
    RecyclerView.Adapter<ShipmentAdapter.ShipmentViewHolder>() {

    class ShipmentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val trackingId: TextView = view.findViewById(R.id.shipmentTrackingId)
        val date: TextView = view.findViewById(R.id.shipmentDate)
        val sender: TextView = view.findViewById(R.id.shipmentSender)
        val receiver: TextView = view.findViewById(R.id.shipmentReceiver)
        val weight: TextView = view.findViewById(R.id.shipmentWeight)
        val cost: TextView = view.findViewById(R.id.shipmentCost)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShipmentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_shipment, parent, false)
        return ShipmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShipmentViewHolder, position: Int) {
        val shipment = shipments[position]
        holder.trackingId.text = shipment.trackingId
        holder.sender.text = shipment.sender
        holder.receiver.text = shipment.receiver
        holder.weight.text = "${shipment.weight} KG"
        holder.cost.text = "₹${shipment.cost}"

        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val shipmentTime = shipment.createdAt?.toDate()?.time ?: System.currentTimeMillis()
        holder.date.text = sdf.format(Date(shipmentTime))
    }

    override fun getItemCount() = shipments.size
}
