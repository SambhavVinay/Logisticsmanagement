package com.example.madecie3

import android.content.Intent
import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OrderAdapter(private val list: List<String>, private val context: android.content.Context) :
    RecyclerView.Adapter<OrderAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val id: TextView = view.findViewById(R.id.orderId)
        val status: TextView = view.findViewById(R.id.orderStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val orderId = list[position]
        holder.id.text = orderId
        holder.status.text = "In Transit"

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ShipmentDetailsActivity::class.java)
            intent.putExtra("id", orderId)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = list.size
}