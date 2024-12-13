package com.capstone.lensakulitku.view.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.lensakulitku.R
import com.capstone.lensakulitku.databinding.FragmentChatBinding

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ChatAdapter
    private val chatMessages = mutableListOf<ChatMessage>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve data from arguments
        val doctorName = arguments?.getString("doctorName") ?: "Unknown Doctor"
        val doctorSpecialization = arguments?.getString("doctorSpecialization") ?: "Unknown Specialization"
        val diseaseName = arguments?.getString("diseaseName") ?: "Unknown Disease"
        val severity = arguments?.getString("severity") ?: "Unknown Severity"
        val imageUri = arguments?.getString("imageUri") // Keep as String?

        setupRecyclerView()
        setupChatData(diseaseName, severity, imageUri, doctorName, doctorSpecialization)

        binding.btnEndChat.setOnClickListener {
            navigateToHome()
        }

        binding.btnSend.setOnClickListener {
            val message = binding.etMessageInput.text.toString().trim()
            if (message.isNotEmpty()) {
                chatMessages.add(ChatMessage(type = ChatMessage.Type.MESSAGE, text = message))
                adapter.notifyItemInserted(chatMessages.size - 1)
                binding.etMessageInput.text?.clear()
                binding.rvChat.scrollToPosition(chatMessages.size - 1)
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = ChatAdapter(chatMessages)
        binding.rvChat.layoutManager = LinearLayoutManager(requireContext())
        binding.rvChat.adapter = adapter
    }

    private fun setupChatData(
        diseaseName: String,
        severity: String,
        imageUri: String?,
        doctorName: String,
        doctorSpecialization: String
    ) {
        // Add analysis result as the first chat
        chatMessages.add(
            ChatMessage(
                type = ChatMessage.Type.ANALYSIS_RESULT,
                diseaseName = diseaseName,
                severity = severity,
                imageUri = imageUri
            )
        )
        adapter.notifyItemInserted(0)

        // Set doctor details
        binding.tvDoctorName.text = doctorName
        binding.tvDoctorSpecialization.text = doctorSpecialization
    }


    private fun navigateToHome() {
        findNavController().navigate(R.id.action_chatFragment_to_homeFragment)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
