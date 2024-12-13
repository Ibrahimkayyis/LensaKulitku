package com.capstone.lensakulitku.view.tracking

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.lensakulitku.R
import com.capstone.lensakulitku.databinding.FragmentTrackingBinding
import data.model.ViewModelFactory

class TrackingFragment : Fragment() {

    private var _binding: FragmentTrackingBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: TrackingViewModel
    private lateinit var adapter: TrackingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrackingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi ViewModel
        viewModel = ViewModelProvider(this, ViewModelFactory(requireContext()))[TrackingViewModel::class.java]

        setupRecyclerView()
        setupSearch()
        setupObservers()
        setupFilterButtons()
    }

    private fun setupRecyclerView() {
        adapter = TrackingAdapter { trackingData ->
            val action = TrackingFragmentDirections.actionTrackingFragmentToTrackingResultFragment(
                trackingData.newImageUri,
                trackingData.diseaseName,
                trackingData.severityLevel,
                trackingData.baselineImageUri,
                trackingData.baselineSeverity,
                trackingData.newSeverity
            )
            findNavController().navigate(action)
        }

        binding.rvTracking.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTracking.adapter = adapter
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupObservers() {
        viewModel.trackingList.observe(viewLifecycleOwner) { list ->
            if (list.isNotEmpty()) {
                binding.tvErrorMessage.isVisible = false
                adapter.setData(list)
            } else {
                binding.tvErrorMessage.isVisible = true
                binding.tvErrorMessage.text = getString(R.string.no_tracking_results_found)
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.pbLoading.isVisible = isLoading
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupFilterButtons() {
        binding.filterToggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.btnAll -> {
                        adapter.filterBySeverity("all")
                        updateToggleButtonState(R.id.btnAll)
                    }
                    R.id.btnSevere -> {
                        adapter.filterBySeverity("Severe")
                        updateToggleButtonState(R.id.btnSevere)
                    }
                    R.id.btnModerate -> {
                        adapter.filterBySeverity("Moderate")
                        updateToggleButtonState(R.id.btnModerate)
                    }
                    R.id.btnMild -> {
                        adapter.filterBySeverity("Mild")
                        updateToggleButtonState(R.id.btnMild)
                    }
                }
            }
        }
    }

    private fun updateToggleButtonState(activeButtonId: Int) {
        val buttons = listOf(
            binding.btnAll,
            binding.btnSevere,
            binding.btnModerate,
            binding.btnMild
        )
        buttons.forEach { button ->
            if (button.id == activeButtonId) {
                button.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blue))
                button.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
            } else {
                button.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.white))
                button.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
