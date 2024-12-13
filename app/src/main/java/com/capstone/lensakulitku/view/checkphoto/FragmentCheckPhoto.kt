package com.capstone.lensakulitku.view.checkphoto

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.capstone.lensakulitku.R
import com.capstone.lensakulitku.databinding.FragmentCheckPhotoBinding

class FragmentCheckPhoto : Fragment() {

    private var _binding: FragmentCheckPhotoBinding? = null
    private val binding get() = _binding!!

    private var imageUri: Uri? = null
    private var baselineImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCheckPhotoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get image URI and baseline image URI from arguments
        arguments?.let {
            imageUri = it.getParcelable("imageUri")
            baselineImageUri = it.getParcelable("baselineImageUri")
        }

        if (imageUri != null) {
            binding.imagePreview.setImageURI(imageUri)
        } else {
            Toast.makeText(requireContext(), "No image to display", Toast.LENGTH_SHORT).show()
        }

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
        val bundle = Bundle().apply {
            imageUri?.let { putParcelable("imageUri", it) }
            baselineImageUri?.let { putParcelable("baselineImageUri", it) }
        }
        findNavController().navigate(R.id.action_checkPhotoFragment_to_describeSymptomsFragment, bundle)
    }

    private fun navigateToRetakePhoto() {
        findNavController().navigate(R.id.action_checkPhotoFragment_to_cameraXFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
