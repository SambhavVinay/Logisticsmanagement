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
        val status: TextView = view.findViewById(R.id.shipmentStatus)
        val detailsContainer: View = view.findViewById(R.id.shipmentDetailsContainer)
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
        holder.status.text = shipment.status
        
        if (shipment.status == "Delivered") {
            holder.status.setTextColor(android.graphics.Color.parseColor("#10B981")) // emerald_500
        } else {
            holder.status.setTextColor(android.graphics.Color.parseColor("#E53935")) // accent_red
        }

        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val shipmentTime = shipment.createdAt?.toDate()?.time ?: System.currentTimeMillis()
        holder.date.text = sdf.format(Date(shipmentTime))

        var isExpanded = false
        holder.itemView.setOnClickListener {
            isExpanded = !isExpanded
            holder.detailsContainer.visibility = if (isExpanded) View.VISIBLE else View.GONE
        }
    }

    override fun getItemCount() = shipments.size
}
