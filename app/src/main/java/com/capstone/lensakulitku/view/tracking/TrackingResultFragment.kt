package com.capstone.lensakulitku.view.tracking

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.capstone.lensakulitku.R
import com.capstone.lensakulitku.databinding.FragmentTrackingResultBinding
import data.model.ViewModelFactory
import java.util.*

class TrackingResultFragment : Fragment() {

    private var _binding: FragmentTrackingResultBinding? = null
    private val binding get() = _binding!!

    private var baselineImageUri: Uri? = null
    private var baselineSeverity: String? = null
    private var newImageUri: Uri? = null
    private var newSeverity: String? = null
    private lateinit var viewModel: TrackingViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrackingResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi ViewModel
        viewModel = ViewModelProvider(this, ViewModelFactory(requireContext()))[TrackingViewModel::class.java]

        // Retrieve arguments
        arguments?.let {
            baselineImageUri = it.getParcelable("baselineImageUri")
            newImageUri = it.getParcelable("newImageUri")
            baselineSeverity = it.getString("baselineSeverity")
            newSeverity = it.getString("newSeverity")
        }

        Log.d("TrackingResultFragment", "baselineImageUri: $baselineImageUri")
        Log.d("TrackingResultFragment", "newImageUri: $newImageUri")
        Log.d("TrackingResultFragment", "baselineSeverity: $baselineSeverity")
        Log.d("TrackingResultFragment", "newSeverity: $newSeverity")

        if (!validateInputs()) {
            return
        }

        displayResults()
        setupButtonListeners()
    }

    private fun validateInputs(): Boolean {
        if (newImageUri == null) {
            Log.e("TrackingResultFragment", "New image URI is missing.")
            Toast.makeText(requireContext(), "New image URI is missing!", Toast.LENGTH_SHORT).show()
            binding.ivNewImage.setImageResource(R.drawable.ic_error_image)
            return false
        }

        if (baselineImageUri == null) {
            Log.w("TrackingResultFragment", "Baseline image URI is missing.")
        }

        return true
    }

    private fun displayResults() {
        try {
            // Load baseline image if available
            if (baselineImageUri != null) {
                Glide.with(this)
                    .load(baselineImageUri)
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_error_image)
                    .into(binding.ivBaselineImage)
            } else {
                binding.ivBaselineImage.setImageResource(R.drawable.ic_error_image)
            }

            // Load new image
            Glide.with(this)
                .load(newImageUri)
                .placeholder(R.drawable.ic_image_placeholder)
                .error(R.drawable.ic_error_image)
                .into(binding.ivNewImage)

            // Display severities and set their colors
            binding.tvBaselineSeverity.text = baselineSeverity ?: "Unknown"
            binding.tvNewSeverity.text = newSeverity ?: "Unknown"
            binding.tvBaselineSeverity.setTextColor(getSeverityColor(baselineSeverity))
            binding.tvNewSeverity.setTextColor(getSeverityColor(newSeverity))

            // Compare severities and display recommendations
            binding.tvRecommendation.text = compareSeverityAndGetRecommendation()
        } catch (e: Exception) {
            Log.e("TrackingResultFragment", "Error displaying results: ${e.message}", e)
            Toast.makeText(requireContext(), "Failed to load images or display results!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getSeverityColor(severity: String?): Int {
        return when (severity?.lowercase()) {
            "mild" -> ContextCompat.getColor(requireContext(), R.color.green) // Mild -> Green
            "moderate" -> ContextCompat.getColor(requireContext(), R.color.yellow) // Moderate -> Yellow
            "severe" -> ContextCompat.getColor(requireContext(), R.color.red) // Severe -> Red
            else -> ContextCompat.getColor(requireContext(), R.color.black) // Default
        }
    }

    private fun compareSeverityAndGetRecommendation(): String {
        val baselineLevel = severityLevel(baselineSeverity)
        val newLevel = severityLevel(newSeverity)

        return when {
            baselineLevel == newLevel -> "Severity remains the same. Continue monitoring and following your current treatment."
            newLevel > baselineLevel -> "Condition has worsened. Consult a doctor for further evaluation."
            newLevel < baselineLevel -> "Condition has improved! Keep up the good care and follow the recommended treatment."
            else -> "Unable to determine the condition. Please consult a healthcare professional."
        }
    }

    private fun severityLevel(severity: String?): Int {
        return when (severity?.lowercase()) {
            "mild" -> 1
            "moderate" -> 2
            "severe" -> 3
            else -> 0
        }
    }

    private fun setupButtonListeners() {
        binding.btnDone.setOnClickListener {
            saveTrackingResult()
            // Navigasi ke TrackingFragment setelah berhasil menyimpan
            findNavController().navigate(R.id.action_trackingResultFragment_to_trackingFragment)
        }

        binding.btnTrackAgain.setOnClickListener {
            findNavController().navigate(R.id.action_trackingResultFragment_to_selectBaselineFragment)
        }
    }

    private fun saveTrackingResult() {
        if (newSeverity == null || newImageUri == null) {
            Toast.makeText(requireContext(), "Unable to save tracking result. Missing data.", Toast.LENGTH_SHORT).show()
            return
        }

        val trackingData = TrackingData(
            newImageUri = newImageUri!!,
            diseaseName = arguments?.getString("diseaseName") ?: "Unknown Disease",
            severityLevel = newSeverity ?: "Unknown",
            createdAt = Date(),
            baselineImageUri = baselineImageUri,
            baselineSeverity = baselineSeverity,
            newSeverity = newSeverity
        )

        viewModel.insertTrackingData(trackingData)

        Toast.makeText(requireContext(), "Tracking result saved successfully!", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
