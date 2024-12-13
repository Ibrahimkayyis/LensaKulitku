package com.capstone.lensakulitku.view.history

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.capstone.lensakulitku.R

class HistoryAdapter(private val onItemClick: (DataItem) -> Unit) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    private val fullList = mutableListOf<DataItem>()
    private val filteredList = mutableListOf<DataItem>()

    fun setData(list: List<DataItem>) {
        fullList.clear()
        fullList.addAll(list)
        filteredList.clear()
        filteredList.addAll(list)
        notifyDataSetChanged()
    }

    fun filterBySeverity(severity: String) {
        if (severity == "all") {
            filteredList.clear()
            filteredList.addAll(fullList)
        } else {
            filteredList.clear()
            filteredList.addAll(fullList.filter { it.severity?.lowercase() == severity.lowercase() })
        }
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        filteredList.clear()
        if (query.isEmpty()) {
            filteredList.addAll(fullList)
        } else {
            filteredList.addAll(fullList.filter {
                it.namaPenyakit?.contains(query, ignoreCase = true) == true
            })
        }
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = filteredList[position]
        Log.d("HistoryAdapter", "ID: ${item.id}, Baseline URI: ${item.baselineImageUri}")
        holder.bind(item)
        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = filteredList.size

    inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvAnalysisTitle: TextView = itemView.findViewById(R.id.tvAnalysisTitle)
        private val tvDiseaseName: TextView = itemView.findViewById(R.id.tvDiseaseName)
        private val tvSeverityText: TextView = itemView.findViewById(R.id.tvSeverityText)
        private val tvCreatedAt: TextView = itemView.findViewById(R.id.tvCreatedAt)
        private val viewSeverityDot: View = itemView.findViewById(R.id.viewSeverityDot)
        private val ivThumbnail: ImageView = itemView.findViewById(R.id.ivThumbnail)

        fun bind(item: DataItem) {
            val analysisId = item.id ?: 0
            tvAnalysisTitle.text = "Analysis #$analysisId"

            tvDiseaseName.text = item.namaPenyakit ?: "Unknown Disease"

            val severity = item.severity?.lowercase() ?: "unknown"
            val severityText = when (severity) {
                "severe" -> "Severe"
                "moderate" -> "Moderate"
                "mild" -> "Mild"
                else -> "Unknown"
            }
            tvSeverityText.text = "Level of Severity : $severityText"

            val dotColor = when (severity) {
                "mild" -> Color.parseColor("#00C853") // Hijau
                "moderate" -> Color.parseColor("#FFC107") // Kuning
                "severe" -> Color.parseColor("#D50000") // Merah
                else -> Color.GRAY // Default
            }
            val backgroundDrawable: Drawable = DrawableCompat.wrap(viewSeverityDot.background)
            DrawableCompat.setTint(backgroundDrawable, dotColor)
            viewSeverityDot.background = backgroundDrawable

            val dateText = item.createdAt ?: "No date"
            tvCreatedAt.text = dateText

            // Load image dengan Glide
            Glide.with(itemView.context)
                .load(item.imageUri)
                .transform(CenterCrop(), RoundedCorners(24))
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(ivThumbnail)
        }
    }
}
