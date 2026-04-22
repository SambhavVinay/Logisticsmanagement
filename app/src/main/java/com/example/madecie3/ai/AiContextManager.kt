package com.example.madecie3.ai

import android.content.Context
import com.example.madecie3.api.RetrofitClient
import com.example.madecie3.data.AppDatabase
import java.text.SimpleDateFormat
import java.util.*

object AiContextManager {

    suspend fun buildSystemPrompt(context: Context): String {
        val sb = StringBuilder()
        sb.append("You are the ShipWise Logistics AI Assistant. Your goal is to help users manage their shipments, track orders, and estimate costs.\n\n")

        // 1. App Knowledge
        sb.append("APP CONTEXT & RULES:\n")
        sb.append("- App Name: ShipWise Logistics\n")
        sb.append("- Shipping Rates: ₹50 per Kilogram (KG) for all zones.\n")
        sb.append("- Tracking IDs: User-created shipments start with 'TRK' followed by numbers.\n")
        sb.append("- Current Date: ${SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())}\n\n")

        // 2. Fetch Inventory (Products)
        try {
            val productResponse = RetrofitClient.api.getProducts()
            if (productResponse.isSuccessful && productResponse.body() != null) {
                sb.append("CURRENT PRODUCT INVENTORY:\n")
                productResponse.body()!!.take(10).forEach { p ->
                    sb.append("- ID ${p.id}: ${p.title} ($${p.price}) [Category: ${p.category}]\n")
                }
                sb.append("\n")
            }
        } catch (e: Exception) {
            sb.append("Inventory data is currently unavailable via API.\n\n")
        }

        // 3. Fetch User Shipment Memory (Local DB)
        try {
            val db = AppDatabase.getDatabase(context)
            val shipments = db.shipmentDao().getAllShipments()
            if (shipments.isNotEmpty()) {
                sb.append("USER'S SHIPMENT HISTORY (Local Memory):\n")
                shipments.take(15).forEach { s ->
                    val date = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date(s.timestamp))
                    sb.append("- [$date] Tracking ${s.trackingId}: ${s.weight}kg from ${s.sender} to ${s.receiver}. Cost: ₹${s.cost}. Method: ${s.paymentMethod}.\n")
                }
                sb.append("\n")
            } else {
                sb.append("The user has not created any shipments yet.\n\n")
            }
        } catch (e: Exception) {
            sb.append("Local shipment history is currently unavailable.\n\n")
        }

        sb.append("STRICT PERVIEW & PERSONALITY:\n")
        sb.append("- IDENTITY: You are an elite, minimal logistics operator. Your speech is precise, low-latency, and strictly professional.\n")
        sb.append("- BREVITY: Never use more than two sentences. Keep it human and direct. NO PARAGRAPHS.\n")
        sb.append("- FORMATTING: Do not use bullet points or lists unless explicitly requested. Do not use emojis.\n")
        sb.append("- TONE: High-end, technical, and slightly cold but efficient. Talk like an expert, not a support bot.\n")
        sb.append("- ACTION TAGS: If creating a shipment, state the confirmation briefly and append [ACTION:CREATE_SHIPMENT|SENDER:name|RECEIVER:name|PICKUP:addr|DELIVERY:addr|WEIGHT:num] at the very end.\n")
        sb.append("- No greetings like 'Hello! How can I help you today?' if the conversation is ongoing. Just answer.")
        
        return sb.toString()
    }
}
