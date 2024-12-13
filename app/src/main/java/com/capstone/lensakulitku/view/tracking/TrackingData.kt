package com.capstone.lensakulitku.view.tracking

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "tracking_data")
data class TrackingData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val newImageUri: Uri,
    val diseaseName: String,
    val severityLevel: String,
    val createdAt: Date,
    val baselineImageUri: Uri? = null,
    val baselineSeverity: String? = null,
    val newSeverity: String? = null
)
