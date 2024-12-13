package com.capstone.lensakulitku.utils

import android.content.Context
import android.content.SharedPreferences

class AppTourPreferences(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("AppTourPrefs", Context.MODE_PRIVATE)

    companion object {
        private const val TOUR_COMPLETED_KEY = "TOUR_COMPLETED"
    }

    var isTourCompleted: Boolean
        get() = sharedPreferences.getBoolean(TOUR_COMPLETED_KEY, false)
        set(value) {
            sharedPreferences.edit().putBoolean(TOUR_COMPLETED_KEY, value).apply()
        }

    var isScanTourCompleted: Boolean
        get() = sharedPreferences.getBoolean("SCAN_TOUR_COMPLETED", false)
        set(value) {
            sharedPreferences.edit().putBoolean("SCAN_TOUR_COMPLETED", value).apply()
        }

}
