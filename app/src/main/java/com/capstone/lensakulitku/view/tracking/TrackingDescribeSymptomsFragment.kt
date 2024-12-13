package com.capstone.lensakulitku.view.tracking

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.capstone.lensakulitku.R
import com.capstone.lensakulitku.databinding.FragmentTrackingDescribeSymptomsBinding
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class TrackingDescribeSymptomsFragment : Fragment() {

    private var _binding: FragmentTrackingDescribeSymptomsBinding? = null
    private val binding get() = _binding!!

    private var imageUri: Uri? = null
    private var baselineImageUri: Uri? = null
    private var baselineSeverity: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrackingDescribeSymptomsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve the image URI, baseline image URI, and baseline severity from arguments
        arguments?.let {
            imageUri = it.getParcelable("imageUri")
            baselineImageUri = it.getParcelable("baselineImageUri")
            baselineSeverity = it.getString("baselineSeverity")
        }

        if (imageUri == null) {
            showToastAndNavigateBack("Image data missing!")
            return
        }

        Log.d("TrackingDescribeSymptoms", "Received imageUri: $imageUri")
        Log.d("TrackingDescribeSymptoms", "Received baselineImageUri: $baselineImageUri")
        Log.d("TrackingDescribeSymptoms", "Received baselineSeverity: $baselineSeverity")

        setupDropdown()
        setupCheckboxListeners()
        setupButtonListeners()
    }

    private fun setupDropdown() {
        val durations = listOf("Days to Week", "Weeks to Month", "Month to Year")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, durations)
        binding.symptomDurationSpinner.adapter = adapter
    }

    private fun setupCheckboxListeners() {
        binding.checkboxItching.setOnCheckedChangeListener { _, isChecked ->
            Log.d("TrackingSymptoms", "Itching: $isChecked")
        }
        binding.checkboxBleeding.setOnCheckedChangeListener { _, isChecked ->
            Log.d("TrackingSymptoms", "Bleeding: $isChecked")
        }
    }

    private fun setupButtonListeners() {
        binding.btnContinue.setOnClickListener {
            navigateToLoadingPage()
        }
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun uriToFile(uri: Uri, context: Context): File? {
        return try {
            val contentResolver: ContentResolver = context.contentResolver
            val file = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val outputStream: FileOutputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            outputStream.close()
            inputStream?.close()
            file
        } catch (e: Exception) {
            Log.e("TrackingSymptoms", "Failed to convert URI to File: ${e.message}", e)
            null
        }
    }

    private fun navigateToLoadingPage() {
        if (imageUri == null) {
            showToast("Image data is missing!")
            return
        }

        val imageFile = uriToFile(imageUri!!, requireContext())
        if (imageFile == null) {
            showToast("Failed to process image file!")
            return
        }

        val baselineImageFile = baselineImageUri?.let { uriToFile(it, requireContext()) }
        if (baselineImageUri != null && baselineImageFile == null) {
            showToast("Failed to process baseline image file!")
            return
        }

        Log.d("TrackingDescribeSymptoms", "Prepared imageFile: $imageFile")
        Log.d("TrackingDescribeSymptoms", "Prepared baselineImageFile: $baselineImageFile")

        val bundle = Bundle().apply {
            putSerializable("imageFile", imageFile)
            baselineImageFile?.let { putSerializable("baselineImageFile", it) }
            baselineImageUri?.let { putParcelable("baselineImageUri", it) }
            baselineSeverity?.let { putString("baselineSeverity", it) }
        }

        Log.d(
            "TrackingDescribeSymptoms",
            "Navigating to LoadingPage with imageFile: $imageFile, baselineImageUri: $baselineImageUri, and baselineSeverity: $baselineSeverity"
        )

        findNavController().navigate(
            R.id.action_trackingDescribeSymptomsFragment_to_trackingAnalysisLoadingFragment,
            bundle
        )
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showToastAndNavigateBack(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
