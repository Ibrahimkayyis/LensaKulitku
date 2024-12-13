package com.capstone.lensakulitku.view.tracking

import android.content.Context
import android.net.Uri
import com.capstone.lensakulitku.view.history.DataItem
import java.io.File

fun resolveBaselineImageUri(context: Context, dataItem: DataItem): Uri? {
    val baselineDir = File(context.filesDir, "baseline_images")
    val baselineImageFile = File(baselineDir, "baseline_${dataItem.id}.jpg")
    return if (baselineImageFile.exists()) {
        Uri.fromFile(baselineImageFile)
    } else {
        null
    }
}
