package com.capstone.lensakulitku.view.tracking

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.capstone.lensakulitku.R
import com.capstone.lensakulitku.databinding.FragmentAnalysisLoadingBinding
import com.capstone.lensakulitku.view.history.HistoryViewModel
import com.capstone.lensakulitku.view.loading.AnalysisViewModel
import com.capstone.lensakulitku.view.loading.PredictionResponse
import data.model.ViewModelFactory
import java.io.File

class TrackingAnalysisLoadingFragment : Fragment() {

    private var _binding: FragmentAnalysisLoadingBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AnalysisViewModel
    private lateinit var currentImageFile: File
    private var baselineImageFile: File? = null
    private var baselineImageUri: Uri? = null
    private var baselineSeverity: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalysisLoadingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this, ViewModelFactory(requireContext()))[AnalysisViewModel::class.java]

        // Retrieve arguments
        arguments?.let {
            currentImageFile = it.getSerializable("imageFile") as? File
                ?: run {
                    showErrorDialog("Image file is missing!")
                    return
                }
            baselineImageFile = it.getSerializable("baselineImageFile") as? File
            baselineImageUri = it.getParcelable("baselineImageUri")
            baselineSeverity = it.getString("baselineSeverity") // Retrieve baseline severity
        }

        Log.d("TrackingAnalysisLoading", "currentImageFile: $currentImageFile")
        Log.d("TrackingAnalysisLoading", "baselineImageFile: $baselineImageFile")
        Log.d("TrackingAnalysisLoading", "baselineImageUri: $baselineImageUri")
        Log.d("TrackingAnalysisLoading", "baselineSeverity: $baselineSeverity")

        // Start prediction process
        startPrediction()
    }

    private fun startPrediction() {
        viewModel.predictDisease(currentImageFile)
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.predictionResult.observe(viewLifecycleOwner) { result ->
            result?.let {
                val newSeverity = it.data?.severity
                saveAndNavigateToResultFragment(it, newSeverity)
            } ?: showErrorDialog("Prediction result is invalid!")
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                showErrorDialog(it)
            }
        }
    }

    private fun saveAndNavigateToResultFragment(result: PredictionResponse, newSeverity: String?) {
        // Save analysis result to the database using HistoryViewModel
        val historyViewModel = ViewModelProvider(
            this,
            ViewModelFactory(requireContext())
        )[HistoryViewModel::class.java]

        // Retrieve disease name from result
        val namaPenyakit = result.data?.namaPenyakit ?: "Unknown Disease"
        val severity = newSeverity ?: "unknown"
        val severityLevel = when (severity.lowercase()) {
            "mild" -> 1
            "moderate" -> 2
            "severe" -> 3
            else -> 0
        }
        val suggestion = result.data?.suggestion?.joinToString(", ") ?: "No suggestion available."
        val description = result.data?.description ?: "No description available."
        val userId = 1 // Example user ID
        val createdAt = System.currentTimeMillis().toString()

        historyViewModel.saveAnalysisResult(
            context = requireContext(),
            severity = severity,
            severityLevel = severityLevel,
            namaPenyakit = namaPenyakit,
            suggestion = suggestion,
            description = description,
            imageUri = Uri.fromFile(currentImageFile),
            baselineImageUri = baselineImageUri,
            userId = userId,
            createdAt = createdAt
        )

        navigateToResultFragment(result, severity)
    }


    private fun navigateToResultFragment(result: PredictionResponse, newSeverity: String?) {
        val newImageUri = Uri.fromFile(currentImageFile)
        val diseaseName = result.data?.namaPenyakit ?: "Unknown Disease"

        val bundle = Bundle().apply {
            putParcelable("result", result)
            putParcelable("newImageUri", newImageUri)
            putString("newSeverity", newSeverity)
            putString("diseaseName", diseaseName) // Pass disease name
            baselineImageUri?.let { putParcelable("baselineImageUri", it) }
            baselineSeverity?.let { putString("baselineSeverity", it) }
        }

        Log.d(
            "TrackingAnalysisLoading",
            "Navigating to ResultFragment with diseaseName: $diseaseName, newImageUri: $newImageUri, baselineImageUri: $baselineImageUri, baselineSeverity: $baselineSeverity, newSeverity: $newSeverity"
        )

        findNavController().navigate(
            R.id.action_trackingAnalysisLoadingFragment_to_trackingResultFragment,
            bundle
        )
    }



    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("Retry") { _, _ ->
                startPrediction()
            }
            .setNegativeButton("Retake Photo") { _, _ ->
                findNavController().navigate(R.id.action_trackingAnalysisLoadingFragment_to_trackingCameraXFragment)
            }
            .setCancelable(false)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
