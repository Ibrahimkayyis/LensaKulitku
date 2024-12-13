package com.capstone.lensakulitku.view.home

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.capstone.lensakulitku.R
import com.capstone.lensakulitku.databinding.FragmentHomeBinding
import com.capstone.lensakulitku.utils.AppTourPreferences
import com.capstone.lensakulitku.view.history.DataItem
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var appTourPreferences: AppTourPreferences

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appTourPreferences = AppTourPreferences(requireContext())

        homeViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                    return HomeViewModel(requireContext()) as T
                }
                throw IllegalArgumentException(getString(R.string.unknown_viewmodel_class))
            }
        })[HomeViewModel::class.java]

        homeViewModel.latestAnalysis.observe(viewLifecycleOwner) { dataItem ->
            if (dataItem != null) {
                showRecentAnalysis(dataItem)
            } else {
                hideRecentAnalysis()
            }
        }

        if (isFirstLogin()) {
            startAppTour()
        } else {
            updateMedicalProfileStatus()
        }

        binding.medicalProfileCard.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_medicalProfileFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        updateMedicalProfileStatus()
        homeViewModel.fetchLatestAnalysis()
    }

    private fun updateMedicalProfileStatus() {
        val sharedPreferences = requireContext().getSharedPreferences(
            getString(R.string.shared_preferences_key), Context.MODE_PRIVATE
        )
        val isMedicalProfileCompleted = sharedPreferences.getBoolean(
            getString(R.string.medical_profile_completed_key), false
        )

        binding.profileProgressBar.progress = if (isMedicalProfileCompleted) 100 else 0
        binding.progressPercentage.text =
            if (isMedicalProfileCompleted) getString(R.string.completed_percentage) else getString(R.string.not_completed_percentage)
        binding.profileStatusText.text =
            if (isMedicalProfileCompleted) getString(R.string.completed_status) else getString(R.string.not_completed_status)
    }

    private fun showRecentAnalysis(dataItem: DataItem) {
        binding.recentAnalysisTitle.visibility = View.VISIBLE
        binding.recentAnalysisCard.visibility = View.VISIBLE

        binding.recentTvAnalysisTitle.text = getString(R.string.analysis_number, dataItem.id ?: 0)
        binding.recentTvDiseaseName.text =
            dataItem.namaPenyakit ?: getString(R.string.unknown_disease)

        val severity = dataItem.severity?.lowercase() ?: "unknown"
        val severityTextStr = when (severity) {
            "severe" -> getString(R.string.severe)
            "moderate" -> getString(R.string.moderate)
            "mild" -> getString(R.string.mild)
            else -> getString(R.string.unknown)
        }
        binding.recentTvSeverityText.text =
            getString(R.string.level_of_severity, severityTextStr)

        val dotColor = when (severity) {
            "mild" -> Color.parseColor("#00C853") // Hijau
            "moderate" -> Color.parseColor("#FFC107") // Kuning
            "severe" -> Color.parseColor("#D50000") // Merah
            else -> Color.GRAY
        }

        // Set warna dot
        val backgroundDrawable: Drawable = DrawableCompat.wrap(binding.recentViewSeverityDot.background)
        DrawableCompat.setTint(backgroundDrawable, dotColor)
        binding.recentViewSeverityDot.background = backgroundDrawable

        // Set warna teks severity berdasarkan severity level
        val severityTextColor = when (severity) {
            "mild" -> Color.parseColor("#00C853") // Hijau
            "moderate" -> Color.parseColor("#FFC107") // Kuning
            "severe" -> Color.parseColor("#D50000") // Merah
            else -> Color.GRAY
        }
        binding.recentTvSeverityText.setTextColor(severityTextColor)

        binding.recentTvCreatedAt.text = dataItem.createdAt ?: getString(R.string.no_date)

        Glide.with(this)
            .load(dataItem.imageUri)
            .transform(CenterCrop(), RoundedCorners(24))
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(binding.recentIvThumbnail)

        binding.recentAnalysisCard.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToDetailHistoryFragment(dataItem)
            findNavController().navigate(action)
        }
    }


    private fun hideRecentAnalysis() {
        binding.recentAnalysisTitle.visibility = View.GONE
        binding.recentAnalysisCard.visibility = View.GONE
    }

    private fun startAppTour() {
        TapTargetSequence(requireActivity())
            .targets(
                TapTarget.forView(
                    binding.medicalProfileCard,
                    "My Medical Profile",
                    "Complete your medical profile before analyzing your skin condition so we can better assist you."
                )
                    .outerCircleColor(R.color.colorPrimary)
                    .outerCircleAlpha(0.7f)
                    .targetCircleColor(android.R.color.white)
                    .titleTextSize(20)
                    .descriptionTextSize(16)
                    .textColor(android.R.color.white)
                    .dimColor(R.color.black)
                    .drawShadow(true)
                    .cancelable(false)
                    .tintTarget(false),

                TapTarget.forView(
                    requireActivity().findViewById(R.id.cameraTab),
                    "Scan Camera",
                    "You can analyse and track your skin condition with scan camera."
                )
                    .outerCircleColor(R.color.colorPrimary)
                    .outerCircleAlpha(0.7f)
                    .targetCircleColor(R.color.blue)
                    .titleTextSize(20)
                    .descriptionTextSize(16)
                    .textColor(android.R.color.white)
                    .dimColor(R.color.black)
                    .drawShadow(true)
                    .cancelable(false)
                    .tintTarget(false),

                TapTarget.forView(
                    requireActivity().findViewById(R.id.historyTab),
                    "Analysis History",
                    "All information related to your skin condition (results, photos, recommendations, etc) is stored in history."
                )
                    .outerCircleColor(R.color.colorPrimary)
                    .outerCircleAlpha(0.7f)
                    .targetCircleColor(R.color.white)
                    .titleTextSize(20)
                    .descriptionTextSize(16)
                    .textColor(android.R.color.white)
                    .dimColor(R.color.black)
                    .drawShadow(true)
                    .cancelable(false)
                    .tintTarget(false),

                TapTarget.forView(
                    requireActivity().findViewById(R.id.trackingTab),
                    "Tracking Results",
                    "All your tracking history will be stored in Tracking Results."
                )
                    .outerCircleColor(R.color.colorPrimary)
                    .outerCircleAlpha(0.7f)
                    .targetCircleColor(R.color.white)
                    .titleTextSize(20)
                    .descriptionTextSize(16)
                    .textColor(android.R.color.white)
                    .dimColor(R.color.black)
                    .drawShadow(true)
                    .cancelable(false)
                    .tintTarget(false)
            )
            .listener(object : TapTargetSequence.Listener {
                override fun onSequenceFinish() {
                    appTourPreferences.isTourCompleted = true
                    markFirstLoginComplete() // Mark first login completed
                    showMedicalProfileDialog()
                }

                override fun onSequenceStep(tapTarget: TapTarget, targetClicked: Boolean) {}
                override fun onSequenceCanceled(tapTarget: TapTarget?) {}
            })
            .start()
    }

    private fun isFirstLogin(): Boolean {
        val sharedPreferences = requireContext().getSharedPreferences(
            getString(R.string.shared_preferences_key), Context.MODE_PRIVATE
        )
        return sharedPreferences.getBoolean(getString(R.string.is_first_login_key), true)
    }

    private fun markFirstLoginComplete() {
        val sharedPreferences = requireContext().getSharedPreferences(
            getString(R.string.shared_preferences_key), Context.MODE_PRIVATE
        )
        sharedPreferences.edit().putBoolean(getString(R.string.is_first_login_key), false).apply()
    }

    private fun showMedicalProfileDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.medical_profile_dialog_title))
            .setMessage(getString(R.string.medical_profile_dialog_message))
            .setPositiveButton(getString(R.string.continue_button)) { _, _ ->
                findNavController().navigate(R.id.action_homeFragment_to_medicalProfileFragment)
            }
            .setCancelable(false)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
