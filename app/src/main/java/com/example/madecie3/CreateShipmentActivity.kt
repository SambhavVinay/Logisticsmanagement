package com.example.madecie3


import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class CreateShipmentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_shipment)

        val sender = findViewById<EditText>(R.id.sender)
        val receiver = findViewById<EditText>(R.id.receiver)
        val weight = findViewById<EditText>(R.id.weight)
        val btn = findViewById<Button>(R.id.proceedPaymentBtn)

        btn.setOnClickListener {
            if (sender.text.isEmpty() || receiver.text.isEmpty() || weight.text.isEmpty()) {
                Toast.makeText(this, "Fill required fields", Toast.LENGTH_SHORT).show()
            } else {
                val w = weight.text.toString().toDouble()
                val amount = (w * 50).toInt() // simple pricing

                val intent = Intent(this, PaymentActivity::class.java)
                intent.putExtra("amount", amount)
                startActivity(intent)
            }
        }
    }
}