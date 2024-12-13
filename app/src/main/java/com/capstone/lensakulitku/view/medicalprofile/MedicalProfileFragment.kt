package com.capstone.lensakulitku.view.medicalprofile

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.capstone.lensakulitku.R
import com.capstone.lensakulitku.databinding.FragmentMedicalProfileBinding
import data.model.MedicalProfileRequest
import data.model.MedicalProfileViewModel
import data.model.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MedicalProfileFragment : Fragment() {

    private var _binding: FragmentMedicalProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MedicalProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMedicalProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = ViewModelFactory(requireContext())
        viewModel = ViewModelProvider(this, factory)[MedicalProfileViewModel::class.java]

        val sharedPreferences = requireContext().getSharedPreferences("LensaKulitkuPrefs", Context.MODE_PRIVATE)
        val userIdFromPrefs = sharedPreferences.getString("USER_ID", null)

        if (!userIdFromPrefs.isNullOrEmpty()) {
            viewModel.setUserId(userIdFromPrefs)
            Log.d("MedicalProfileFragment", "Loaded User ID: $userIdFromPrefs")
            viewModel.getUserProfile(userIdFromPrefs)
        } else {
            Toast.makeText(requireContext(), "User ID not found. Please login again.", Toast.LENGTH_SHORT).show()
            return
        }

        setupDatePicker()
        observeViewModel()

        binding.saveButton.setOnClickListener {
            saveMedicalProfile()
        }
    }

    private fun setupDatePicker() {
        binding.dateOfBirthLayout.setEndIconOnClickListener {
            showDatePicker()
        }
        binding.dateOfBirthInput.setOnClickListener {
            showDatePicker()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val datePicker = android.app.DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val date = String.format("%02d-%02d-%04d", dayOfMonth, month + 1, year)
                binding.dateOfBirthInput.setText(date)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    private fun saveMedicalProfile() {
        val fullname = binding.fullNameInput.text.toString().trim()
        val gender = binding.genderInput.text.toString().trim().lowercase()
        val dateOfBirth = formatDateToISO(binding.dateOfBirthInput.text.toString().trim())
        val address = binding.addressInput.text.toString().trim()
        val city = binding.cityInput.text.toString().trim()
        val postcode = binding.postcodeInput.text.toString().trim()
        val skintype = binding.skinTypeInput.text.toString().trim()
        val medicalHistory = binding.medicalHistoryInput.text.toString().trim().ifEmpty { "no" }
        val allergies = binding.allergiesInput.text.toString().trim().ifEmpty { "no" }
        val medication = binding.medicationInput.text.toString().trim().ifEmpty { "no" }

        if (fullname.isEmpty() || !isValidGender(gender) || dateOfBirth.isEmpty() || postcode.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all required fields correctly.", Toast.LENGTH_SHORT).show()
            return
        }

        val profileRequest = MedicalProfileRequest(
            fullname = fullname,
            Gender = gender,
            DateOfBirth = dateOfBirth,
            Adress = address,
            city = city,
            postcode = postcode,
            Skintype = skintype,
            medicalhistory = medicalHistory,
            alergies = allergies,
            medication = medication
        )

        val userId = viewModel.userId.value
        if (userId.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "User ID not found. Please log in again.", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("API_REQUEST", "Updating user profile with ID: $userId")
        Log.d("API_REQUEST", "Payload: $profileRequest")

        viewModel.updateUserProfile(userId, profileRequest)
    }

    private fun observeViewModel() {
        viewModel.updateResult.observe(viewLifecycleOwner) { result ->
            if (result != null) {
                showSuccessDialog()
            } else {
                Toast.makeText(requireContext(), "Failed to update profile.", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.saveButton.isEnabled = !isLoading
        }

        viewModel.userProfile.observe(viewLifecycleOwner) { profile ->
            if (profile != null) {
                populateProfileData(profile)
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            if (!error.isNullOrEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun populateProfileData(profile: MedicalProfileRequest) {
        binding.fullNameInput.setText(profile.fullname)
        binding.genderInput.setText(profile.Gender)
        binding.dateOfBirthInput.setText(formatISOToDisplayDate(profile.DateOfBirth))
        binding.addressInput.setText(profile.Adress)
        binding.cityInput.setText(profile.city)
        binding.postcodeInput.setText(profile.postcode)
        binding.skinTypeInput.setText(profile.Skintype)
        binding.medicalHistoryInput.setText(profile.medicalhistory)
        binding.allergiesInput.setText(profile.alergies)
        binding.medicationInput.setText(profile.medication)
    }

    private fun formatDateToISO(date: String): String {
        return try {
            val inputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)
            val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val parsedDate = inputFormat.parse(date)
            outputFormat.format(parsedDate!!)
        } catch (e: Exception) {
            ""
        }
    }

    private fun formatISOToDisplayDate(date: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)
            val parsedDate = inputFormat.parse(date)
            outputFormat.format(parsedDate!!)
        } catch (e: Exception) {
            ""
        }
    }

    private fun isValidGender(gender: String): Boolean {
        return gender == "male" || gender == "female"
    }

    private fun showSuccessDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Thank You!")
        builder.setMessage("Your medical profile has been updated successfully.")
        builder.setPositiveButton("Continue") { dialog, _ ->
            dialog.dismiss()
            val sharedPreferences = requireContext().getSharedPreferences("LensaKulitkuPrefs", Context.MODE_PRIVATE)
            sharedPreferences.edit().putBoolean("MEDICAL_PROFILE_COMPLETED", true).apply()
            navigateToHomeFragment()
        }
        builder.setCancelable(false)
        builder.show()
    }

    private fun navigateToHomeFragment() {
        findNavController().navigate(R.id.action_medicalProfileFragment_to_homeFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
