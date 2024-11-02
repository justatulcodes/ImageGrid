package com.advait.org.assignment.data.network

import android.content.ContentValues.TAG
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapFactory.Options
import android.util.Log
import com.advait.org.assignment.data.cache.DiskLruCache
import com.advait.org.assignment.domain.LRUMemoryCache
import com.advait.org.assignment.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.URL
import kotlin.coroutines.cancellation.CancellationException


/**
 * Downloads and scales a thumbnail image from the given URL.
 */
suspend fun downloadThumbnail(
    imageUrl: String,
    targetWidth: Int,
    targetHeight: Int
): Bitmap? = withContext(Dispatchers.IO) {
    try {
        val url = URL(imageUrl)
        val conn = url.openConnection().apply {
            connectTimeout = 5000
            readTimeout = 5000
        }

        if (!isActive) return@withContext null

        val options = Options().apply {
            inJustDecodeBounds = true
        }

        conn.getInputStream().use { inputStream ->
            BitmapFactory.decodeStream(inputStream, null, options)
        }

        ensureActive()

        options.apply {
            inSampleSize = calculateInSampleSize(this, targetWidth, targetHeight)
            inJustDecodeBounds = false
            inPreferredConfig = Bitmap.Config.RGB_565
        }

        URL(imageUrl).openStream().use { stream ->
            BitmapFactory.decodeStream(stream, null, options)
        }
    } catch (e: CancellationException) {
        Log.d("Job Cancelled", "CancellationException: ${e.message}")
        throw e
    } catch (e: IOException) {
        Log.e(TAG, "Network error: ${e.message}", e)
        null
    } catch (e: Exception) {
        Log.e(TAG, "Unexpected error: ${e.message}", e)
        null
    }
}

/**
 * Downloads a full-size image from the given URL
 */
suspend fun downloadFullSizeImage(imageUrl: String): Bitmap? = withContext(Dispatchers.IO) {
    try {
        val url = URL(imageUrl)
        val conn = url.openConnection().apply {
            connectTimeout = 5000
            readTimeout = 5000
        }

        val options = Options().apply {
            inPreferredConfig = Bitmap.Config.RGB_565
        }

        ensureActive()

        conn.getInputStream().use { inputStream ->
            BitmapFactory.decodeStream(inputStream, null, options)
        }
    } catch (e: Exception) {
        Log.e(TAG, "Error downloading full size image: ${e.message}", e)
        null
    }
}

/**
 * Calculates the optimal inSampleSize for decoding the image.
 */
private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
    val (height: Int, width: Int) = options.outHeight to options.outWidth
    var inSampleSize = 1

    if (height > reqHeight || width > reqWidth) {
        val halfHeight: Int = height / 2
        val halfWidth: Int = width / 2

        while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
            inSampleSize *= 2
        }
    }
    return inSampleSize
}