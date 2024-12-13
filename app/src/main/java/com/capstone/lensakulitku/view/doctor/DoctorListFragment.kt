package com.capstone.lensakulitku.view.doctor

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.lensakulitku.R
import com.capstone.lensakulitku.databinding.FragmentDoctorListBinding

class DoctorListFragment : Fragment() {

    private var _binding: FragmentDoctorListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: DoctorAdapter

    // Variables to hold the data from ResultAnalysisFragment
    private var diseaseName: String? = null
    private var severity: String? = null
    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDoctorListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve data from the previous fragment
        arguments?.let {
            diseaseName = it.getString("diseaseName")
            severity = it.getString("severity")
            imageUri = it.getString("imageUri")?.let { uriString -> Uri.parse(uriString) }
        }

        setupHeader()
        setupRecyclerView()
    }

    private fun setupHeader() {
        binding.tvHeaderTitle.text = getString(R.string.list_doctor_title)
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupRecyclerView() {
        val doctorList = getDoctorList()

        adapter = DoctorAdapter(doctorList) { doctor ->
            // Pass data to ChatFragment along with the selected doctor details
            val bundle = Bundle().apply {
                putInt("doctorId", doctor.id)
                putString("doctorName", doctor.name)
                putString("doctorSpecialization", doctor.specialization)
                putString("diseaseName", diseaseName)
                putString("severity", severity)
                putString("imageUri", imageUri?.toString())
            }

            findNavController().navigate(R.id.action_doctorListFragment_to_chatFragment, bundle)
        }

        binding.rvDoctors.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDoctors.adapter = adapter
    }

    private fun getDoctorList(): List<Doctor> {
        return listOf(
            Doctor(1, "Dr. Andi Wijaya", "Dermatologist", R.drawable.doctor_1),
            Doctor(2, "Dr. Sinta Ramli", "Skin Specialist", R.drawable.doctor_2),
            Doctor(3, "Dr. Bambang Setiawan", "Aesthetic Dermatologist", R.drawable.doctor_3),
            Doctor(4, "Dr. Lisa Amelia", "Cosmetic Dermatologist", R.drawable.doctor_4)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
