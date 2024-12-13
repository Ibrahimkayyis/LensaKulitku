package com.capstone.lensakulitku.view.scan

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.lensakulitku.R
import com.capstone.lensakulitku.databinding.FragmentScanBinding
import com.capstone.lensakulitku.utils.AppTourPreferences
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence

class ScanFragment : Fragment() {

    private var _binding: FragmentScanBinding? = null
    private val binding get() = _binding!!
    private lateinit var appTourPreferences: AppTourPreferences

    private val options = listOf(
        ScanOption(
            title = "Analysis",
            description = "Take Photos of the affected area, describe your symptoms and receive immediate results and recommendations",
            imageRes = R.drawable.analysis_image
        ),
        ScanOption(
            title = "Tracker",
            description = "Keep track of your skin and store photos in your case file",
            imageRes = R.drawable.tracker_image
        )
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize AppTourPreferences
        appTourPreferences = AppTourPreferences(requireContext())

        val adapter = ScanOptionAdapter(options) { option ->
            when (option.title) {
                "Analysis" -> navigateToAnalysis()
                "Tracker" -> navigateToTracker()
            }
        }

        binding.rvScanOptions.layoutManager = LinearLayoutManager(requireContext())
        binding.rvScanOptions.adapter = adapter

        // Start App Tour if it's the first time opening this fragment
        if (isFirstTimeOpeningFragment()) {
            startAppTour()
        }
    }

    private fun navigateToAnalysis() {
        findNavController().navigate(R.id.action_scanFragment_to_cameraXFragment)
    }

    private fun navigateToTracker() {
        // Navigasi ke SelectBaselineFragment
        findNavController().navigate(R.id.action_scanFragment_to_selectBaselineFragment)
    }

    private fun startAppTour() {
        // Access the Start button in the first item of RecyclerView
        binding.rvScanOptions.post {
            val analysisButton = binding.rvScanOptions.findViewHolderForAdapterPosition(0)
                ?.itemView
                ?.findViewById<View>(R.id.btnStart)

            if (analysisButton != null) {
                TapTargetSequence(requireActivity())
                    .targets(
                        TapTarget.forView(
                            analysisButton,
                            "Start Your First Analysis",
                            "Tap the 'Start' button to begin your first skin analysis and explore the features."
                        )
                            .outerCircleColor(R.color.colorPrimary)
                            .outerCircleAlpha(0.9f)
                            .targetCircleColor(android.R.color.white)
                            .titleTextSize(20)
                            .descriptionTextSize(16)
                            .textColor(android.R.color.white)
                            .dimColor(android.R.color.black)
                            .drawShadow(true)
                            .cancelable(false)
                            .tintTarget(true)
                            .transparentTarget(true)
                    )
                    .listener(object : TapTargetSequence.Listener {
                        override fun onSequenceFinish() {
                            // Mark App Tour as completed for this fragment
                            markFragmentVisited()
                            Toast.makeText(requireContext(), "App Tour Completed", Toast.LENGTH_SHORT).show()
                        }

                        override fun onSequenceStep(tapTarget: TapTarget, targetClicked: Boolean) {
                            // Optional: Handle actions per step
                        }

                        override fun onSequenceCanceled(tapTarget: TapTarget?) {
                            Toast.makeText(requireContext(), "App Tour Skipped", Toast.LENGTH_SHORT).show()
                        }
                    })
                    .start()
            } else {
                Toast.makeText(requireContext(), "Start button not found for App Tour.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isFirstTimeOpeningFragment(): Boolean {
        val sharedPreferences = requireContext().getSharedPreferences(
            getString(R.string.shared_preferences_key),
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getBoolean(getString(R.string.is_first_time_scan_fragment_key), true)
    }

    private fun markFragmentVisited() {
        val sharedPreferences = requireContext().getSharedPreferences(
            getString(R.string.shared_preferences_key),
            Context.MODE_PRIVATE
        )
        sharedPreferences.edit().putBoolean(getString(R.string.is_first_time_scan_fragment_key), false).apply()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
