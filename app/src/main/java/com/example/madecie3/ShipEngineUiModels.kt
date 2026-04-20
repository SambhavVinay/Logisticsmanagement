package com.example.madecie3

data class ShipmentCard(
    val shipmentId: String,
    val title: String,
    val status: String,
    val amount: String,
    val category: String
)

data class LabelCard(
    val labelId: String,
    val trackingNumber: String,
    val status: String
)
