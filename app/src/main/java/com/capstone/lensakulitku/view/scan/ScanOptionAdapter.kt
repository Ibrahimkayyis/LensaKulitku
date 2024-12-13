package com.capstone.lensakulitku.view.scan

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.capstone.lensakulitku.databinding.ItemScanOptionBinding

data class ScanOption(
    val title: String,
    val description: String,
    val imageRes: Int
)

class ScanOptionAdapter(
    private val options: List<ScanOption>,
    private val onStartClick: (ScanOption) -> Unit
) : RecyclerView.Adapter<ScanOptionAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemScanOptionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(option: ScanOption) {
            binding.tvOptionTitle.text = option.title
            binding.tvOptionDescription.text = option.description
            binding.ivOptionImage.setImageResource(option.imageRes)
            binding.btnStart.setOnClickListener { onStartClick(option) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemScanOptionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(options[position])
    }

    override fun getItemCount(): Int = options.size
}
