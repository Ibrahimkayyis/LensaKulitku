package com.capstone.lensakulitku.view.more

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.capstone.lensakulitku.databinding.ItemMoreOptionBinding

class MoreOptionsAdapter(
    private val options: List<MoreOptionItem>,
    private val onItemClicked: (MoreOptionItem) -> Unit
) : RecyclerView.Adapter<MoreOptionsAdapter.MoreOptionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoreOptionViewHolder {
        val binding = ItemMoreOptionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MoreOptionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MoreOptionViewHolder, position: Int) {
        holder.bind(options[position])
    }

    override fun getItemCount(): Int = options.size

    inner class MoreOptionViewHolder(private val binding: ItemMoreOptionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(option: MoreOptionItem) {
            binding.ivOptionIcon.setImageResource(option.iconResId)
            binding.tvOptionTitle.text = option.title

            binding.root.setOnClickListener {
                onItemClicked(option)
            }
        }
    }
}
