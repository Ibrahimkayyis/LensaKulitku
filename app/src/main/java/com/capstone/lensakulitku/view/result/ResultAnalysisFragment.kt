package com.capstone.lensakulitku.view.result

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.capstone.lensakulitku.R
import com.capstone.lensakulitku.databinding.FragmentResultAnalysisBinding
import com.capstone.lensakulitku.view.history.HistoryDatabase
import com.capstone.lensakulitku.view.history.HistoryEntity
import com.capstone.lensakulitku.view.loading.PredictionResponse
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToInt

class ResultAnalysisFragment : Fragment() {

    private var _binding: FragmentResultAnalysisBinding? = null
    private val binding get() = _binding!!

    private var predictionResult: PredictionResponse? = null
    private var imageUri: Uri? = null
    private var baselineImageUri: Uri? = null

    private var isRecommendationExpanded = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultAnalysisBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        predictionResult = arguments?.getParcelable("result")
        imageUri = arguments?.getParcelable("imageUri")
        baselineImageUri = arguments?.getParcelable("baselineImageUri")

        displayResult()
        setupButtonListeners()
        setupRecommendationToggle()
    }

    private fun displayResult() {
        predictionResult?.data?.let { data ->
            Glide.with(this)
                .load(imageUri)
                .transform(CenterCrop(), RoundedCorners(24))
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(binding.imagePreview)

            binding.tvDiseaseName.text = data.namaPenyakit ?: getString(R.string.unknown_disease)
            binding.tvDescription.text = data.description ?: getString(R.string.no_description)

            val severityText = data.severity?.capitalize() ?: getString(R.string.unknown)
            binding.tvSeverity.text = getString(R.string.severity, severityText)

            // Set text color based on severity
            val severityColor = when (data.severity?.lowercase()) {
                getString(R.string.severity_mild).lowercase() -> Color.parseColor("#00C853") // Green
                getString(R.string.severity_moderate).lowercase() -> Color.parseColor("#FFC107") // Yellow
                getString(R.string.severity_severe).lowercase() -> Color.parseColor("#D50000") // Red
                else -> Color.BLACK // Default color
            }
            binding.tvSeverity.setTextColor(severityColor)

            val diseaseScore = data.confidenceScore?.disease ?: 0f
            val severityScore = data.confidenceScore?.severity ?: 0f

            binding.tvConfidenceScore.text = getString(R.string.confidence_score)
            setProgressWithColor(binding.pbDisease, diseaseScore)
            binding.tvDiseaseConfidence.text = getString(R.string.disease_confidence, diseaseScore.roundToInt())

            binding.tvSeverityConfidence.visibility = View.GONE
            binding.pbSeverityScore.visibility = View.GONE

            binding.tvSuggestions.text =
                data.suggestion?.joinToString("\n") ?: getString(R.string.no_suggestions)
            binding.suggestionsContainer.visibility = View.GONE

            saveToLocalDatabase(data, baselineImageUri)
        } ?: run {
            binding.tvDiseaseName.text = getString(R.string.analysis_failed)
            binding.tvDescription.text = getString(R.string.try_again)
        }
    }


    private fun saveToLocalDatabase(data: com.capstone.lensakulitku.view.loading.Data, baselineImageUri: Uri?) {
        val db = HistoryDatabase.getInstance(requireContext())

        val resolvedBaselineUri = baselineImageUri ?: imageUri

        val uniqueFileName = getString(R.string.analysis_file_name, System.currentTimeMillis())
        val file = File(requireContext().filesDir, uniqueFileName)
        val newImageUri: Uri? = try {
            val inputStream = requireContext().contentResolver.openInputStream(imageUri!!)
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            outputStream.close()
            inputStream?.close()

            Uri.fromFile(file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

        val uniqueId = System.currentTimeMillis().toInt()

        val historyEntity = HistoryEntity(
            id = uniqueId,
            severity = data.severity,
            severityLevel = data.severityLevel,
            createdAt = data.createdAt,
            userId = null,
            namaPenyakit = data.namaPenyakit,
            suggestion = data.suggestion?.joinToString("\n"),
            description = data.description,
            imageUri = newImageUri?.toString(),
            baselineImageUri = resolvedBaselineUri?.toString()
        )

        viewLifecycleOwner.lifecycleScope.launch {
            db.historyDao().insertHistory(historyEntity)
        }
    }

    private fun setupButtonListeners() {
        binding.btnBackToHome.setOnClickListener {
            findNavController().navigate(R.id.action_resultAnalysisFragment_to_homeFragment)
        }

        // New Button: Consult with Doctor
        binding.btnConsultWithDoctor.setOnClickListener {
            val bundle = Bundle().apply {
                putString("diseaseName", predictionResult?.data?.namaPenyakit)
                putString("severity", predictionResult?.data?.severity)
                putString("imageUri", imageUri?.toString())
            }
            findNavController().navigate(R.id.action_resultAnalysisFragment_to_doctorListFragment, bundle)
        }
    }

    private fun setupRecommendationToggle() {
        binding.recommendationHeader.setOnClickListener {
            isRecommendationExpanded = !isRecommendationExpanded
            binding.suggestionsContainer.visibility =
                if (isRecommendationExpanded) View.VISIBLE else View.GONE
            binding.ivArrow.rotation = if (isRecommendationExpanded) 90f else 0f
        }
    }

    private fun setProgressWithColor(progressBar: View, value: Float) {
        if (progressBar is android.widget.ProgressBar) {
            val progressValue = value.roundToInt().coerceIn(0, 100)
            progressBar.progress = progressValue

            val color = when {
                progressValue <= 33 -> Color.parseColor("#00C853")
                progressValue <= 66 -> Color.parseColor("#FFC107")
                else -> Color.parseColor("#D50000")
            }

            val drawable: Drawable = DrawableCompat.wrap(progressBar.progressDrawable)
            DrawableCompat.setTint(drawable, color)
            progressBar.progressDrawable = drawable
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
