package com.example.madecie3


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class OrdersActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)

        val recycler = findViewById<RecyclerView>(R.id.recyclerView)

        val dummyList = listOf("TRK1234", "TRK5678", "TRK9012")

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = OrderAdapter(dummyList, this)
    }
}