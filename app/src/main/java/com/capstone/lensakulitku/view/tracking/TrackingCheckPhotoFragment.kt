package com.capstone.lensakulitku.view.tracking

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.capstone.lensakulitku.R
import com.capstone.lensakulitku.databinding.FragmentTrackingCheckPhotoBinding

class TrackingCheckPhotoFragment : Fragment() {

    private var _binding: FragmentTrackingCheckPhotoBinding? = null
    private val binding get() = _binding!!

    private var imageUri: Uri? = null
    private var baselineImageUri: Uri? = null
    private var baselineSeverity: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrackingCheckPhotoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get image URIs and baselineSeverity from arguments
        arguments?.let {
            imageUri = it.getParcelable("imageUri")
            baselineImageUri = it.getParcelable("baselineImageUri")
            baselineSeverity = it.getString("baselineSeverity")
        }

        // Display image
        if (imageUri != null) {
            binding.imagePreview.setImageURI(imageUri)
        } else {
            Toast.makeText(requireContext(), "No image to display", Toast.LENGTH_SHORT).show()
            Log.e("TrackingCheckPhoto", "imageUri is missing in arguments!")
        }

        Log.d("TrackingCheckPhoto", "baselineImageUri: $baselineImageUri")
        Log.d("TrackingCheckPhoto", "baselineSeverity: $baselineSeverity")

        // Back Button Listener
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Continue Button Listener
        binding.btnContinue.setOnClickListener {
            navigateToDescribeSymptoms()
        }

        // Retake Photo Button Listener
        binding.btnRetakePhoto.setOnClickListener {
            navigateToRetakePhoto()
        }
    }

    private fun navigateToDescribeSymptoms() {
        if (imageUri == null) {
            Toast.makeText(requireContext(), "Image Data Missing", Toast.LENGTH_SHORT).show()
            return
        }

        val bundle = Bundle().apply {
            putParcelable("imageUri", imageUri)
            baselineImageUri?.let { putParcelable("baselineImageUri", it) }
            baselineSeverity?.let { putString("baselineSeverity", it) }
        }

        Log.d(
            "TrackingCheckPhotoFragment",
            "Navigating to DescribeSymptoms with imageUri: $imageUri, baselineImageUri: $baselineImageUri, and baselineSeverity: $baselineSeverity"
        )

        findNavController().navigate(
            R.id.action_trackingCheckPhotoFragment_to_trackingDescribeSymptomsFragment,
            bundle
        )
    }

    private fun navigateToRetakePhoto() {
        findNavController().navigate(R.id.action_trackingCheckPhotoFragment_to_trackingCameraXFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
