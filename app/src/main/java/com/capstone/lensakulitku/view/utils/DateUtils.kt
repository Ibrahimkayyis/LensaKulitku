package com.capstone.lensakulitku.view.utils

import java.text.SimpleDateFormat
import java.util.Locale

fun formatDate(date: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)
    val parsedDate = inputFormat.parse(date)
    return outputFormat.format(parsedDate)
}