package com.example.madecie3

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.madecie3.ai.AiMessage

class ChatAdapter(private val messages: List<AiMessage>) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val messageContainer: LinearLayout = view.findViewById(R.id.messageContainer)
        val messageText: TextView = view.findViewById(R.id.messageText)
        val aiAccent: View = view.findViewById(R.id.aiAccentBorder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_message, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = messages[position]
        holder.messageText.text = message.content

        if (message.role == "user") {
            holder.aiAccent.visibility = View.GONE
            holder.messageText.setTextColor(holder.itemView.context.getColor(R.color.accent_red))
            holder.messageText.text = "> ${message.content}"
        } else {
            holder.aiAccent.visibility = View.VISIBLE
            holder.messageText.setTextColor(holder.itemView.context.getColor(R.color.white))
        }
    }

    override fun getItemCount() = messages.size
}
