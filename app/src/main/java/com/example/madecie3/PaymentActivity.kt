package com.example.madecie3


import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class PaymentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        val amount = intent.getIntExtra("amount", 0)
        val amountText = findViewById<TextView>(R.id.amountText)
        val payBtn = findViewById<Button>(R.id.payBtn)

        amountText.text = "Amount: ₹$amount"

        payBtn.setOnClickListener {
            val trackingId = "TRK" + Random.nextInt(1000, 9999)

            val intent = Intent(this, OrderConfirmationActivity::class.java)
            intent.putExtra("trackingId", trackingId)
            startActivity(intent)
        }
    }
}