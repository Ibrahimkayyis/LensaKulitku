package com.capstone.lensakulitku.view.tracking

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.lensakulitku.R
import com.capstone.lensakulitku.databinding.FragmentSelectBaselineBinding
import com.capstone.lensakulitku.view.history.DataItem
import com.capstone.lensakulitku.view.history.HistoryAdapter
import com.capstone.lensakulitku.view.history.HistoryViewModel
import data.model.ViewModelFactory
import java.io.File

class SelectBaselineFragment : Fragment() {

    private var _binding: FragmentSelectBaselineBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HistoryViewModel
    private lateinit var adapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectBaselineBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi ViewModel
        viewModel = ViewModelProvider(this, ViewModelFactory(requireContext()))[HistoryViewModel::class.java]

        setupRecyclerView()
        setupObservers()

        // Ambil data dari database lokal
        viewModel.fetchHistory(requireContext())
    }

    private fun setupRecyclerView() {
        adapter = HistoryAdapter { dataItem ->
            Log.d("SelectBaselineFragment", "ID: ${dataItem.id}, Baseline URI: ${dataItem.baselineImageUri}")

            val baselineUri = resolveBaselineImageUri(dataItem)
            val baselineSeverity = dataItem.severity

            if (baselineUri == null) {
                Toast.makeText(requireContext(), "Baseline image not found for ${dataItem.namaPenyakit}", Toast.LENGTH_SHORT).show()
                Log.e("SelectBaselineFragment", "Baseline image URI is null for dataItem ID: ${dataItem.id}")
                return@HistoryAdapter
            }

            val bundle = Bundle().apply {
                putParcelable("dataItem", dataItem)
                putParcelable("baselineImageUri", baselineUri)
                baselineSeverity?.let { putString("baselineSeverity", it) }
            }

            findNavController().navigate(
                R.id.action_selectBaselineFragment_to_trackingCameraXFragment,
                bundle
            )
        }

        binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistory.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.historyList.observe(viewLifecycleOwner) { list ->
            if (list.isNullOrEmpty().not()) {
                val enrichedList = list.filterNotNull().map { dataItem ->
                    dataItem.copy(
                        baselineImageUri = resolveBaselineImageUri(dataItem)?.toString()
                    )
                }
                adapter.setData(enrichedList)
                binding.tvEmptyMessage.visibility = View.GONE
            } else {
                binding.tvEmptyMessage.visibility = View.VISIBLE
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                Log.e("SelectBaselineFragment", "Error: $it")
            }
        }
    }

    private fun resolveBaselineImageUri(dataItem: DataItem): Uri? {
        // Gunakan baselineImageUri dari dataItem jika tersedia
        if (!dataItem.baselineImageUri.isNullOrEmpty()) {
            return Uri.parse(dataItem.baselineImageUri)
        }

        // Jika tidak ada, fallback ke file lokal
        val baselineDir = File(requireContext().filesDir, "baseline_images")
        val baselineImageFile = File(baselineDir, "baseline_${dataItem.id}.jpg")
        return if (baselineImageFile.exists()) {
            Uri.fromFile(baselineImageFile)
        } else {
            null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
