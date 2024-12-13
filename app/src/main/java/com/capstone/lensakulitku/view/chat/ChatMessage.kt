package com.capstone.lensakulitku.view.chat

data class ChatMessage(
    val type: Type,
    val text: String? = null,
    val diseaseName: String? = null,
    val severity: String? = null,
    val imageUri: String? = null
) {
    enum class Type {
        ANALYSIS_RESULT, MESSAGE
    }
}
