package com.capstone.lensakulitku.view.history

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.lensakulitku.R
import com.capstone.lensakulitku.databinding.FragmentHistoryBinding
import data.model.ViewModelFactory

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HistoryViewModel
    private lateinit var adapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this, ViewModelFactory(requireContext()))[HistoryViewModel::class.java]

        setupRecyclerView()
        setupSearchBar()
        setupObservers()
        setupFilterButtons()

        // Ambil data dari database lokal
        viewModel.fetchHistory(requireContext())
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchHistory(requireContext())
    }

    private fun setupRecyclerView() {
        adapter = HistoryAdapter { dataItem ->
            val action = HistoryFragmentDirections.actionHistoryFragmentToDetailHistoryFragment(dataItem)
            findNavController().navigate(action)
        }

        binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistory.adapter = adapter
    }

    private fun setupSearchBar() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.pbLoading.isVisible = isLoading
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            binding.tvErrorMessage.isVisible = !errorMessage.isNullOrEmpty()
            binding.tvErrorMessage.text = errorMessage
        }

        viewModel.historyList.observe(viewLifecycleOwner) { list ->
            if (list.isNotEmpty()) {
                binding.tvErrorMessage.isVisible = false
                adapter.setData(list)
            } else {
                binding.tvErrorMessage.isVisible = true
                binding.tvErrorMessage.text = getString(R.string.no_history_found)
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
                button.setBackgroundColor(resources.getColor(R.color.blue, null))
                button.setTextColor(resources.getColor(android.R.color.white, null))
            } else {
                button.setBackgroundColor(resources.getColor(android.R.color.white, null))
                button.setTextColor(resources.getColor(android.R.color.black, null))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
