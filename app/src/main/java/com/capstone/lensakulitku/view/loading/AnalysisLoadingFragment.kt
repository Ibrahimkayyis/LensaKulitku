package com.capstone.lensakulitku.view.loading

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.capstone.lensakulitku.R
import com.capstone.lensakulitku.databinding.FragmentAnalysisLoadingBinding
import data.model.ViewModelFactory
import java.io.File

class AnalysisLoadingFragment : Fragment() {

    private var _binding: FragmentAnalysisLoadingBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AnalysisViewModel
    private lateinit var imageFile: File
    private var baselineImageUri: Uri? = null

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

        // Ambil argumen dari fragment sebelumnya
        arguments?.let {
            imageFile = it.getSerializable("imageFile") as File
            baselineImageUri = it.getParcelable("baselineImageUri")
        }

        viewModel.predictDisease(imageFile)
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.predictionResult.observe(viewLifecycleOwner) { result ->
            result?.let {
                val bundle = Bundle().apply {
                    putParcelable("result", it)
                    arguments?.getParcelable<Uri>("imageUri")?.let { uri ->
                        putParcelable("imageUri", uri)
                    }
                    baselineImageUri?.let { uri ->
                        putParcelable("baselineImageUri", uri)
                    }
                }
                findNavController().navigate(
                    R.id.action_analysisLoadingFragment_to_resultAnalysisFragment,
                    bundle
                )
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            message?.let { showErrorDialog(it) }
        }
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.error_title))
            .setMessage(message)
            .setPositiveButton(getString(R.string.retry)) { _, _ ->
                viewModel.predictDisease(imageFile)
            }
            .setNegativeButton(getString(R.string.retake_photo)) { _, _ ->
                findNavController().navigate(R.id.action_analysisLoadingFragment_to_cameraXFragment)
            }
            .setCancelable(false)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
