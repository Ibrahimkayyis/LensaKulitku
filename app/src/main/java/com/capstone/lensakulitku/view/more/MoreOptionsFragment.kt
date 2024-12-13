package com.capstone.lensakulitku.view.more

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.lensakulitku.R
import com.capstone.lensakulitku.databinding.FragmentMoreOptionsBinding
import com.capstone.lensakulitku.view.login.LoginActivity

class MoreOptionsFragment : Fragment() {

    private var _binding: FragmentMoreOptionsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: MoreOptionsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoreOptionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val options = listOf(
            MoreOptionItem(R.drawable.ic_baseline_edit_24, "Edit Medical Profile"),
            MoreOptionItem(R.drawable.ic_baseline_info_24, "About Us"),
            MoreOptionItem(R.drawable.ic_baseline_logout_24, "Log Out")
        )

        adapter = MoreOptionsAdapter(options) { option ->
            when (option.title) {
                "Edit Medical Profile" -> navigateToMedicalProfile()
                "About Us" -> openAboutUs()
                "Log Out" -> showLogoutConfirmationDialog()
            }
        }

        binding.rvMoreOptions.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMoreOptions.adapter = adapter
    }

    private fun navigateToMedicalProfile() {
        findNavController().navigate(R.id.action_moreOptionsFragment_to_medicalProfileFragment)
    }

    private fun openAboutUs() {
        val url = "https://lensakulitku.netlify.app/"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Log Out")
            setMessage("Are you sure you want to log out?")
            setPositiveButton("Yes") { _, _ ->
                logOut()
            }
            setNegativeButton("No", null)
            create()
            show()
        }
    }

    private fun logOut() {
        val sharedPreferences = requireContext().getSharedPreferences("LensaKulitkuPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()

        Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
