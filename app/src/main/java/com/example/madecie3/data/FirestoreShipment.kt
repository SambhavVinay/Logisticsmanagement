package com.example.madecie3.data

import com.google.firebase.Timestamp

data class FirestoreShipment(
    val trackingId: String = "",
    val sender: String = "",
    val receiver: String = "",
    val pickupAddress: String = "",
    val deliveryAddress: String = "",
    val weight: Double = 0.0,
    val cost: Int = 0,
    val paymentMethod: String = "",
    val status: String = "Created",
    val createdAt: Timestamp? = null
)
