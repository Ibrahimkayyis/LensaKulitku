package com.capstone.lensakulitku.view.chat

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone.lensakulitku.databinding.ItemChatAnalysisBinding
import com.capstone.lensakulitku.databinding.ItemChatMessageBinding

class ChatAdapter(private val messages: List<ChatMessage>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_ANALYSIS = 1
        private const val VIEW_TYPE_MESSAGE = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_ANALYSIS) {
            AnalysisViewHolder(ItemChatAnalysisBinding.inflate(inflater, parent, false))
        } else {
            MessageViewHolder(ItemChatMessageBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AnalysisViewHolder -> holder.bind(messages[position])
            is MessageViewHolder -> holder.bind(messages[position])
        }
    }

    override fun getItemCount(): Int = messages.size

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].type == ChatMessage.Type.ANALYSIS_RESULT) VIEW_TYPE_ANALYSIS else VIEW_TYPE_MESSAGE
    }

    class AnalysisViewHolder(private val binding: ItemChatAnalysisBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(message: ChatMessage) {
            binding.tvDiseaseName.text = message.diseaseName
            binding.tvSeverity.text = message.severity

            // Convert imageUri from String to Uri
            val imageUri = message.imageUri?.let { Uri.parse(it) }

            // Load the image using Glide
            Glide.with(binding.root)
                .load(imageUri)
                .into(binding.ivAnalysisImage)
        }
    }

    class MessageViewHolder(private val binding: ItemChatMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: ChatMessage) {
            binding.tvMessage.text = message.text
        }
    }
}
