package com.example.madecie3

import android.content.Intent
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.madecie3.api.Product

class ProductAdapter(
    private val list: List<Product>,
    private val context: android.content.Context
) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.productImage)
        val title: TextView  = view.findViewById(R.id.productTitle)
        val price: TextView  = view.findViewById(R.id.productPrice)
        val category: TextView = view.findViewById(R.id.productCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = list[position]
        holder.title.text    = product.title
        holder.price.text    = "$${"%.2f".format(product.price)}"
        holder.category.text = product.category.replaceFirstChar { it.uppercase() }
        holder.image.load(product.image) {
            crossfade(true)
            placeholder(android.R.drawable.ic_menu_gallery)
        }

        holder.itemView.setOnClickListener {
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
}
