package com.example.madecie3

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import coil.load

class ShipmentDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shipment_details)

        val text  = findViewById<TextView>(R.id.detailText)
        val image = findViewById<ImageView>(R.id.detailImage)

        // Product detail (from ProductAdapter / Dashboard)
        val productId    = intent.getIntExtra("productId", -1)
        val productTitle = intent.getStringExtra("productTitle")
        val productPrice = intent.getDoubleExtra("productPrice", 0.0)
        val productCat   = intent.getStringExtra("productCategory")
        val productImg   = intent.getStringExtra("productImage")
        val productDesc  = intent.getStringExtra("productDescription")

        // Cart detail (from CartAdapter / Orders)
        val cartId     = intent.getIntExtra("cartId", -1)
        val userId     = intent.getIntExtra("userId", -1)
        val itemCount  = intent.getIntExtra("itemCount", 0)

        if (productId != -1 && productTitle != null) {
            image.visibility = View.VISIBLE
            image.load(productImg) { crossfade(true) }
            text.text = """
                📦 Shipment ID: $productId
                📌 Product: $productTitle
                💲 Value: $${"%.2f".format(productPrice)}
                📁 Category: $productCat
                📍 Status: Ready to Ship
                🏙️ Origin: Warehouse A
                🏙️ Destination: Customer
                ─────────────────────
                📝 Description:
                $productDesc
            """.trimIndent()
        } else if (cartId != -1) {
            image.visibility = View.GONE
            text.text = """
                🛒 Order ID: Cart #$cartId
                👤 User ID: $userId
                📦 Total Items: $itemCount
                📍 Status: In Transit
                🏙️ Origin: Distribution Center
                🏙️ Destination: User $userId's Address
                ⏱️ ETA: 2–5 business days
                💳 Payment: Completed
            """.trimIndent()
        } else {
            // Legacy fallback
            val id = intent.getStringExtra("id")
            text.text = """
                Tracking ID: $id
                Status: In Transit
                From: Bangalore
                To: Chennai
                Payment: Paid
            """.trimIndent()
        }
    }
}