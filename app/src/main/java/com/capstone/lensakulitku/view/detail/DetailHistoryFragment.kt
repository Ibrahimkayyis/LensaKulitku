package com.capstone.lensakulitku.view.history

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.capstone.lensakulitku.R
import com.capstone.lensakulitku.databinding.FragmentDetailHistoryBinding

class DetailHistoryFragment : Fragment() {

    private var _binding: FragmentDetailHistoryBinding? = null
    private val binding get() = _binding!!

    private var dataItem: DataItem? = null

    private var isDescriptionExpanded = false
    private var isRecommendationExpanded = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: DetailHistoryFragmentArgs by navArgs()
        dataItem = args.dataItem

        displayDetail()
        setupCollapsibleSections()
        setupButtonListeners()
    }

    private fun displayDetail() {
        dataItem?.let { item ->
            binding.tvDiseaseName.text = item.namaPenyakit ?: getString(R.string.unknown_disease)

            val severity = item.severity?.lowercase() ?: "unknown"
            val severityText = when (severity) {
                "severe" -> getString(R.string.severe)
                "moderate" -> getString(R.string.moderate)
                "mild" -> getString(R.string.mild)
                else -> getString(R.string.unknown)
            }
            binding.tvSeverity.text = getString(R.string.severity, severityText)

            val severityColor = when (severity) {
                "mild" -> Color.parseColor("#00C853") // Hijau
                "moderate" -> Color.parseColor("#FFC107") // Kuning
                "severe" -> Color.parseColor("#D50000") // Merah
                else -> Color.BLACK
            }
            binding.tvSeverity.setTextColor(severityColor)

            binding.tvDescription.text = item.description ?: getString(R.string.no_description)
            binding.descriptionContainer.visibility = View.GONE

            binding.tvSuggestions.text = item.suggestion ?: getString(R.string.no_suggestions)
            binding.suggestionsContainer.visibility = View.GONE

            binding.tvCreatedAt.text = item.createdAt ?: getString(R.string.no_date)

            Glide.with(this)
                .load(item.imageUri)
                .transform(CenterCrop(), RoundedCorners(24))
                .into(binding.imagePreview)

        } ?: run {
            binding.tvDiseaseName.text = getString(R.string.no_data)
        }
    }

    private fun setupCollapsibleSections() {
        binding.descriptionHeader.setOnClickListener {
            isDescriptionExpanded = !isDescriptionExpanded
            binding.descriptionContainer.visibility = if (isDescriptionExpanded) View.VISIBLE else View.GONE
            binding.ivArrowDescription.rotation = if (isDescriptionExpanded) 90f else 0f
        }

        binding.recommendationHeader.setOnClickListener {
            isRecommendationExpanded = !isRecommendationExpanded
            binding.suggestionsContainer.visibility = if (isRecommendationExpanded) View.VISIBLE else View.GONE
            binding.ivArrow.rotation = if (isRecommendationExpanded) 90f else 0f
        }
    }

    private fun setupButtonListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
