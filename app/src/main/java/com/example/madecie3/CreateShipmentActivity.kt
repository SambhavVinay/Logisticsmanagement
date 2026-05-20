package com.example.madecie3

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class CreateShipmentActivity : AppCompatActivity() {

    private var selectedImageUri: Uri? = null

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                selectedImageUri = it
                val preview = findViewById<ImageView>(R.id.packageImagePreview)
                val placeholder = findViewById<LinearLayout>(R.id.packageImagePlaceholder)
                preview.setImageURI(it)
                preview.visibility = android.view.View.VISIBLE
                placeholder.visibility = android.view.View.GONE
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeUtils.applyTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_shipment)
        if (AuthGuard.requireAuthenticated(this) == null) return

        val sender   = findViewById<EditText>(R.id.sender)
        val receiver = findViewById<EditText>(R.id.receiver)
        val weight   = findViewById<EditText>(R.id.weight)
        val pickup   = findViewById<EditText>(R.id.pickup)
        val delivery = findViewById<EditText>(R.id.delivery)
        val btn      = findViewById<Button>(R.id.proceedPaymentBtn)
        val imagePick = findViewById<FrameLayout>(R.id.packageImagePick)
        val senderSection = findViewById<LinearLayout>(R.id.senderSection)

        val productImg = intent.getStringExtra("productImg")
        val productTitle = intent.getStringExtra("productTitle")

        if (!productImg.isNullOrEmpty()) {
            senderSection.visibility = android.view.View.GONE
            // Pre-fill so it doesn't fail validation
            sender.setText(productTitle ?: "Inventory Item")
            pickup.setText("Warehouse A")

            // Load image using Coil
            val preview = findViewById<ImageView>(R.id.packageImagePreview)
            val placeholder = findViewById<LinearLayout>(R.id.packageImagePlaceholder)
            preview.visibility = android.view.View.VISIBLE
            placeholder.visibility = android.view.View.GONE
            
            val request = coil.request.ImageRequest.Builder(this)
                .data(productImg)
                .target(preview)
                .build()
            coil.Coil.imageLoader(this).enqueue(request)
            // Can't pick new image if it's from inventory
            imagePick.setOnClickListener(null)
        } else {
            imagePick.setOnClickListener {
                imagePickerLauncher.launch("image/*")
            }
        }

        btn.setOnClickListener {
            if (sender.text.isEmpty() || receiver.text.isEmpty() || weight.text.isEmpty()) {
                Toast.makeText(this, "Fill required fields", Toast.LENGTH_SHORT).show()
            } else {
                val w = weight.text.toString().toDoubleOrNull() ?: 0.0
                val amount = (w * 50).toInt()
                val intent = Intent(this, PaymentActivity::class.java)
                intent.putExtra("amount", amount)
                intent.putExtra("sender", sender.text.toString())
                intent.putExtra("receiver", receiver.text.toString())
                intent.putExtra("pickup", pickup.text.toString())
                intent.putExtra("delivery", delivery.text.toString())
                intent.putExtra("weight", w)
                // pass image url if from inventory
                if (!productImg.isNullOrEmpty()) {
                    intent.putExtra("productImg", productImg)
                } else if (selectedImageUri != null) {
                    intent.putExtra("productImg", selectedImageUri.toString())
                }
                startActivity(intent)
            }
        }
    }
}