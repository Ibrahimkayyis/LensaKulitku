package com.capstone.lensakulitku.view.tracking

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone.lensakulitku.R
import com.capstone.lensakulitku.databinding.ItemTrackingBinding
import java.text.SimpleDateFormat
import java.util.Locale

class TrackingAdapter(
    private val onItemClicked: (TrackingData) -> Unit
) : RecyclerView.Adapter<TrackingAdapter.TrackingViewHolder>() {

    private val originalList = mutableListOf<TrackingData>()
    private val filteredList = mutableListOf<TrackingData>()

    // Set data for the adapter
    fun setData(data: List<TrackingData>) {
        originalList.clear()
        originalList.addAll(data)
        filteredList.clear()
        filteredList.addAll(data)
        notifyDataSetChanged()
    }

    // Filter the list based on a query
    fun filter(query: String) {
        filteredList.clear()
        if (query.isEmpty()) {
            filteredList.addAll(originalList)
        } else {
            filteredList.addAll(originalList.filter {
                it.diseaseName.contains(query, ignoreCase = true)
            })
        }
        notifyDataSetChanged()
    }

    // Filter the list based on severity level
    fun filterBySeverity(severity: String) {
        filteredList.clear()
        if (severity == "all") {
            filteredList.addAll(originalList)
        } else {
            filteredList.addAll(originalList.filter {
                it.severityLevel.equals(severity, ignoreCase = true)
            })
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackingViewHolder {
        val binding = ItemTrackingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrackingViewHolder, position: Int) {
        holder.bind(filteredList[position])
    }

    override fun getItemCount(): Int = filteredList.size

    inner class TrackingViewHolder(private val binding: ItemTrackingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: TrackingData) {
            with(binding) {
                tvDiseaseName.text = data.diseaseName
                tvSeverityLevel.text = data.severityLevel

                val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
                tvCreatedAt.text = dateFormat.format(data.createdAt)

                // Set severity level color
                val severityColor = when (data.severityLevel.lowercase()) {
                    "mild" -> ContextCompat.getColor(root.context, R.color.green) // Hijau
                    "moderate" -> ContextCompat.getColor(root.context, R.color.yellow) // Kuning
                    "severe" -> ContextCompat.getColor(root.context, R.color.red) // Merah
                    else -> ContextCompat.getColor(root.context, R.color.black) // Default
                }
                tvSeverityLevel.setTextColor(severityColor)

                // Load image using Glide
                Glide.with(ivThumbnail.context)
                    .load(data.newImageUri)
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_error_image)
                    .into(ivThumbnail)

                // Set click listener for the root view
                root.setOnClickListener {
                    onItemClicked(data)
                }
            }
        }
    }
}
