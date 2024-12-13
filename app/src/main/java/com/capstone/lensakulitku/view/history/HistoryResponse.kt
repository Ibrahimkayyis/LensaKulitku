package com.capstone.lensakulitku.view.history

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class HistoryResponse(
	val data: List<DataItem?>? = null,
	val message: String? = null,
	val status: String? = null
) : Parcelable

@Parcelize
data class DataItem(
	val severity: String? = null,
	val severityLevel: Int? = null,
	val createdAt: String? = null,
	val userId: Int? = null,
	@SerializedName("nama_penyakit") val namaPenyakit: String? = null,
	val suggestion: String? = null,
	val description: String? = null,
	val id: Int? = null,
	val imageUri: String? = null,
	var baselineImageUri: String? = null
) : Parcelable

