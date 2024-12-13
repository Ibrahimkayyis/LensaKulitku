package com.capstone.lensakulitku.view.history

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val severity: String?,
    val severityLevel: Int?,
    val createdAt: String?,
    val userId: Int?,
    val namaPenyakit: String?,
    val suggestion: String?,
    val description: String?,
    val imageUri: String?,
    val baselineImageUri: String?
)
