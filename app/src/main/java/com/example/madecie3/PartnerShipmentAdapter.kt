package com.example.madecie3

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.madecie3.data.FirestoreShipment

class PartnerShipmentAdapter(
    private val shipments: List<FirestoreShipment>,
    private val onStatusChange: (String, String) -> Unit
) : RecyclerView.Adapter<PartnerShipmentAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val trackingId: TextView = view.findViewById(R.id.shipmentTrackingId)
        val status: TextView = view.findViewById(R.id.shipmentStatus)
        val sender: TextView = view.findViewById(R.id.shipmentSender)
        val receiver: TextView = view.findViewById(R.id.shipmentReceiver)
        val weight: TextView = view.findViewById(R.id.shipmentWeight)
        val btnNotDelivered: Button = view.findViewById(R.id.btnNotDelivered)
        val btnDelivered: Button = view.findViewById(R.id.btnDelivered)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_partner_shipment, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val shipment = shipments[position]
        holder.trackingId.text = shipment.trackingId
        holder.sender.text = shipment.sender.take(3)
        holder.receiver.text = shipment.receiver.take(3)
        holder.weight.text = "${shipment.weight} KG"
        holder.status.text = shipment.status
        
        if (shipment.status == "Delivered") {
            holder.status.setTextColor(Color.parseColor("#10B981")) // emerald_500
        } else {
            holder.status.setTextColor(Color.parseColor("#E53935")) // accent_red
        }

        holder.btnNotDelivered.setOnClickListener {
            onStatusChange(shipment.trackingId, "Not Delivered")
        }

        holder.btnDelivered.setOnClickListener {
            onStatusChange(shipment.trackingId, "Delivered")
        }
    }

    override fun getItemCount() = shipments.size
}
