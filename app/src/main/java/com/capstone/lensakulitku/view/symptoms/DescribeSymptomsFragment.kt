package com.capstone.lensakulitku.view.describe

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
import com.capstone.lensakulitku.databinding.FragmentDescribeSymptomsBinding
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class DescribeSymptomsFragment : Fragment() {

    private var _binding: FragmentDescribeSymptomsBinding? = null
    private val binding get() = _binding!!

    private var imageUri: Uri? = null
    private var baselineImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDescribeSymptomsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageUri = arguments?.getParcelable("imageUri")
        baselineImageUri = arguments?.getParcelable("baselineImageUri")

        if (imageUri == null) {
            Toast.makeText(requireContext(), getString(R.string.error_image_not_found), Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
            return
        }

        setupDropdown()
        setupCheckboxListeners()
        setupButtonListeners()
    }

    private fun setupDropdown() {
        val durations = resources.getStringArray(R.array.symptom_durations)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, durations)
        binding.symptomDurationSpinner.adapter = adapter
    }

    private fun setupCheckboxListeners() {
        binding.checkboxItching.setOnCheckedChangeListener { _, isChecked ->
            Log.d(TAG, getString(R.string.log_checkbox_itching, isChecked))
        }
        binding.checkboxBleeding.setOnCheckedChangeListener { _, isChecked ->
            Log.d(TAG, getString(R.string.log_checkbox_bleeding, isChecked))
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

    private fun uriToFile(uri: Uri, context: Context): File {
        val contentResolver: ContentResolver = context.contentResolver
        val myFile = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        val outputStream: FileOutputStream = FileOutputStream(myFile)
        inputStream?.copyTo(outputStream)
        outputStream.close()
        inputStream?.close()
        return myFile
    }

    private fun navigateToLoadingPage() {
        if (imageUri == null) {
            Toast.makeText(requireContext(), getString(R.string.error_missing_image), Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val imageFile = uriToFile(imageUri!!, requireContext())

            val bundle = Bundle().apply {
                putSerializable("imageFile", imageFile)
                putParcelable("imageUri", imageUri)
                baselineImageUri?.let { putParcelable("baselineImageUri", it) }
            }
            findNavController().navigate(R.id.action_describeSymptomsFragment_to_analysisLoadingFragment, bundle)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), getString(R.string.error_processing_image, e.message), Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "DescribeSymptoms"
    }
}
