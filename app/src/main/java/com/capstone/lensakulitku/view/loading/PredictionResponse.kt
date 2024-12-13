package com.capstone.lensakulitku.view.loading

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class PredictionResponse(
	val data: Data? = null,
	val message: String? = null,
	val status: String? = null
) : Parcelable

@Parcelize
data class ConfidenceScore(
	val severity: Float? = null,
	val disease: Float? = null
) : Parcelable

@Parcelize
data class Data(
	val severity: String? = null,
	val severityLevel: Int? = null,
	val createdAt: String? = null,
	val confidenceScore: ConfidenceScore? = null,
	@SerializedName("nama_penyakit") val namaPenyakit: String? = null,
	val suggestion: List<String?>? = null,
	val description: String? = null
) : Parcelable

