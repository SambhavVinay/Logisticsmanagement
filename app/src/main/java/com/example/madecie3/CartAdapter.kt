package com.example.madecie3

import android.content.Intent
import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.madecie3.api.Cart

class CartAdapter(
    private val list: List<Cart>,
    private val context: android.content.Context
) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val id: TextView       = view.findViewById(R.id.orderId)
        val status: TextView   = view.findViewById(R.id.orderStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cart = list[position]
        holder.id.text     = "Cart #${cart.id}  •  User ${cart.userId}"
        val totalItems = cart.products.sumOf { it.quantity }
        holder.status.text = "$totalItems item(s)  •  In Transit"

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ShipmentDetailsActivity::class.java)
            intent.putExtra("cartId", cart.id)
            intent.putExtra("userId", cart.userId)
            intent.putExtra("itemCount", totalItems)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = list.size
}
