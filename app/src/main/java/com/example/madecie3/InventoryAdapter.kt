package com.example.madecie3

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.madecie3.api.Product

class InventoryAdapter(
    private var list: List<Product>,
    private val context: android.content.Context
) : RecyclerView.Adapter<InventoryAdapter.ViewHolder>() {

    private var fullList = list.toList()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.invProductImage)
        val title: TextView = view.findViewById(R.id.invProductTitle)
        val price: TextView = view.findViewById(R.id.invProductPrice)
        val category: TextView = view.findViewById(R.id.invProductCategory)
        val desc: TextView = view.findViewById(R.id.invProductDesc)
        val detailsContainer: View = view.findViewById(R.id.invProductDetailsContainer)
        val shipBtn: Button = view.findViewById(R.id.invProductShipBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_inventory_product, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = list[position]
        holder.title.text = product.title
        holder.price.text = "$${"%.2f".format(product.price)}"
        holder.category.text = product.category.replaceFirstChar { it.uppercase() }
        holder.desc.text = product.description
        
        holder.image.load(product.image) {
            crossfade(true)
            placeholder(android.R.drawable.ic_menu_gallery)
        }

        var isExpanded = false
        holder.itemView.setOnClickListener {
            isExpanded = !isExpanded
            holder.detailsContainer.visibility = if (isExpanded) View.VISIBLE else View.GONE
        }

        holder.shipBtn.setOnClickListener {
            val intent = Intent(context, ShipmentDetailsActivity::class.java)
            intent.putExtra("productId", product.id)
            intent.putExtra("productTitle", product.title)
            intent.putExtra("productPrice", product.price)
            intent.putExtra("productCategory", product.category)
            intent.putExtra("productImage", product.image)
            intent.putExtra("productDescription", product.description)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = list.size

    fun filter(query: String) {
        val lowerQuery = query.lowercase()
        list = if (lowerQuery.isEmpty()) {
            fullList
        } else {
            fullList.filter { it.title.lowercase().contains(lowerQuery) || it.category.lowercase().contains(lowerQuery) }
        }
        notifyDataSetChanged()
    }
    
    fun updateData(newList: List<Product>) {
        fullList = newList.toList()
        list = newList.toList()
        notifyDataSetChanged()
    }
}
