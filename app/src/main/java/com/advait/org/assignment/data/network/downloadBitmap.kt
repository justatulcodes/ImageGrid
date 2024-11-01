package com.advait.org.assignment.data.network

import android.content.ContentValues.TAG
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.advait.org.assignment.data.cache.DiskLruCache
import com.advait.org.assignment.domain.Image
import com.advait.org.assignment.domain.LruImageCache
import com.advait.org.assignment.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.URL
import kotlin.coroutines.cancellation.CancellationException

suspend fun downloadBitmap(image: Image, memoryCache: LruImageCache?, diskCache: DiskLruCache?): Bitmap? {

    memoryCache?.getImage(image.imageKey)?.let { bitmap ->
        Log.d("downloadImage", "Image loaded from memory cache")
        return bitmap
    }

    return withContext(Dispatchers.IO) {
        try {
            // Try disk cache first before network call
            val diskBitmap = diskCache?.get(image.imageKey)
            if (diskBitmap != null) {
                Log.d("downloadImage", "Image loaded from disk cache")
                // Cache in memory for faster future access
                memoryCache?.cacheImage(image.imageKey, diskBitmap)
                return@withContext diskBitmap
            }

            // If not in cache, download from network
            val url = URL(image.imageUrl)
            val conn = url.openConnection().apply {
                connectTimeout = 5000
                readTimeout = 5000
            }

            if (!isActive) {
                Log.d("JOB", "Job cancelled before network request")
                return@withContext null
            }

            // First check dimensions without loading the full image
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }

            conn.getInputStream().use { inputStream ->
                BitmapFactory.decodeStream(inputStream, null, options)
            }

            ensureActive()

            // Calculate sample size for the actual load
            options.apply {
                inSampleSize = calculateInSampleSize(
                    this,
                    Constants.THUMBNAIL_IMG_WIDTH,
                    Constants.THUMBNAIL_IMG_HEIGHT
                )
                inJustDecodeBounds = false
                // Use RGB_565 for thumbnails to reduce memory usage by 50%
                inPreferredConfig = Bitmap.Config.RGB_565
            }

            // Load the actual bitmap
            val bitmap = URL(image.imageUrl).openStream().use { stream ->
                BitmapFactory.decodeStream(stream, null, options)
            }

            bitmap?.let {
                // Cache operations in parallel
                launch { memoryCache?.cacheImage(image.imageKey, it) }
                launch { diskCache?.put(image.imageKey, it) }
                Log.d("downloadImage", "Image downloaded and cached")
            }

            bitmap
        } catch (e: CancellationException) {
            Log.d("JOB", "CancellationException: ${e.message}")
            throw e
        } catch (e: IOException) {
            Log.e(TAG, "Network error: ${e.message}", e)
            null
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error: ${e.message}", e)
            null
        }
    }
}
suspend fun downloadFullSizeImage(imageUrl: String): Bitmap? {
    return withContext(Dispatchers.IO) {
        try {
            val url = URL(imageUrl)
            val conn = url.openConnection().apply {
                connectTimeout = 5000
                readTimeout = 5000
            }

            if (isActive) {
                conn.getInputStream().use { inputStream ->
                    BitmapFactory.decodeStream(inputStream)
                }
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
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