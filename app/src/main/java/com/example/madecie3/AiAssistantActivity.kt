package com.example.madecie3

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.madecie3.ai.*
import com.example.madecie3.data.AppDatabase
import com.example.madecie3.data.ShipmentEntity
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers

class AiAssistantActivity : AppCompatActivity() {

    private val messages = mutableListOf<AiMessage>()
    private lateinit var adapter: ChatAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var statusText: TextView
    private var thinkingJob: kotlinx.coroutines.Job? = null
    private val thinkingTerms = listOf("Skiddadling", "Thinking", "Spooking", "Shenanging")

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeUtils.applyTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ai_assistant)

        recyclerView = findViewById(R.id.chatRecyclerView)
        statusText   = findViewById(R.id.chatStatus)
        val input    = findViewById<EditText>(R.id.messageInput)
        val sendBtn  = findViewById<TextView>(R.id.sendBtn)

        adapter = ChatAdapter(messages)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Initial welcome message (Emoji-free, concise)
        addMessage(AiMessage("assistant", "Operator online. Systems integrated. How can I assist with your shipments?"))

        sendBtn.setOnClickListener {
            val query = input.text.toString().trim()
            if (query.isNotEmpty()) {
                input.text.clear()
                handleQuery(query)
            }
        }
    }

    private fun handleQuery(query: String) {
        addMessage(AiMessage("user", query))
        
        startThinkingAnimation()

        lifecycleScope.launch {
            try {
                // 1. Build context
                val systemPrompt = AiContextManager.buildSystemPrompt(this@AiAssistantActivity)
                
                // 2. Prepare request (System context + all history)
                val conversation = mutableListOf<AiMessage>()
                conversation.add(AiMessage("system", systemPrompt))
                
                // Ensure conversation starts with 'user' and skip previous errors
                val historyToSend = messages
                    .filter { !it.content.startsWith("Error:") }
                    .dropWhile { it.role != "user" }
                    
                conversation.addAll(historyToSend)

                val request = AiRequest(messages = conversation)

                // 3. Call API
                val response = AiClient.api.getCompletion(AiClient.getAuthHeader(), request)

                if (response.isSuccessful && response.body() != null) {
                    val aiReply = response.body()!!.choices.firstOrNull()?.message?.content ?: "I'm sorry, I couldn't process that."
                    
                    // Clean reply for UI (remove action tags)
                    val cleanReply = aiReply.replace("\\[ACTION:.*?\\]".toRegex(), "").trim()
                    addMessage(AiMessage("assistant", cleanReply))
                    
                    // Process any background actions
                    parseAndExecuteAction(aiReply)
                } else {
                    addMessage(AiMessage("assistant", "Error: ${response.code()} - ${response.message()}"))
                }
            } catch (e: Exception) {
                addMessage(AiMessage("assistant", "Network failure: ${e.localizedMessage}"))
            } finally {
                stopThinkingAnimation()
            }
        }
    }

    private fun addMessage(message: AiMessage) {
        messages.add(message)
        adapter.notifyItemInserted(messages.size - 1)
        recyclerView.scrollToPosition(messages.size - 1)
    }

    private fun startThinkingAnimation() {
        thinkingJob?.cancel()
        thinkingJob = lifecycleScope.launch {
            var index = 0
            while (true) {
                statusText.text = thinkingTerms[index]
                statusText.setTextColor(getColor(R.color.accent_red))
                index = (index + 1) % thinkingTerms.size
                kotlinx.coroutines.delay(1200)
            }
        }
    }

    private fun stopThinkingAnimation() {
        thinkingJob?.cancel()
        statusText.text = "Online"
        statusText.setTextColor(getColor(R.color.color_success))
    }

    private fun parseAndExecuteAction(reply: String) {
        val pattern = "\\[ACTION:CREATE_SHIPMENT\\|SENDER:(.*?)\\|RECEIVER:(.*?)\\|PICKUP:(.*?)\\|DELIVERY:(.*?)\\|WEIGHT:(.*?)\\]".toRegex()
        val match = pattern.find(reply)
        if (match != null) {
            val sender = match.groups[1]?.value ?: "AI Request"
            val receiver = match.groups[2]?.value ?: "AI Request"
            val pickup = match.groups[3]?.value ?: "N/A"
            val delivery = match.groups[4]?.value ?: "N/A"
            val weightStr = match.groups[5]?.value ?: "1.0"
            val weight = weightStr.toDoubleOrNull() ?: 1.0
            
            val trackingId = "TRKAI${(10000..99999).random()}"
            val cost = (weight * 50).toInt()

            lifecycleScope.launch(Dispatchers.IO) {
                val db = AppDatabase.getDatabase(this@AiAssistantActivity)
                val shipment = ShipmentEntity(
                    sender = sender,
                    receiver = receiver,
                    pickupAddress = pickup,
                    deliveryAddress = delivery,
                    weight = weight,
                    cost = cost,
                    trackingId = trackingId,
                    paymentMethod = "AI Managed"
                )
                db.shipmentDao().insertShipment(shipment)
            }
        }
    }
}
