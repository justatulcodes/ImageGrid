package com.advait.org.assignment.data.network

import android.content.ContentValues.TAG
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.advait.org.assignment.data.cache.DiskLruCache
import com.advait.org.assignment.domain.model.Article
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
 * Downloads the thumbnail image from the given URL.
 *
 * This function is used to download image for grid view. It downloads the image with the
 * height and width specified in the constants.
 */
suspend fun downloadBitmap(article: Article, memoryCache: LRUMemoryCache?, diskCache: DiskLruCache?): Bitmap? {

    memoryCache?.getImage(article.imageKey)?.let { bitmap ->
        return bitmap
    }

    return withContext(Dispatchers.IO) {
        try {
            val diskBitmap = diskCache?.get(article.imageKey)
            if (diskBitmap != null) {
                memoryCache?.putImage(article.imageKey, diskBitmap)
                return@withContext diskBitmap
            }

            val url = URL(article.imageUrl)
            val conn = url.openConnection().apply {
                connectTimeout = 5000
                readTimeout = 5000
            }

            if (!isActive) {
                return@withContext null
            }

            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }

            conn.getInputStream().use { inputStream ->
                BitmapFactory.decodeStream(inputStream, null, options)
            }

            ensureActive()

            options.apply {
                inSampleSize = calculateInSampleSize(
                    this,
                    Constants.THUMBNAIL_IMG_WIDTH,
                    Constants.THUMBNAIL_IMG_HEIGHT
                )
                inJustDecodeBounds = false
                inPreferredConfig = Bitmap.Config.RGB_565
            }

            val bitmap = URL(article.imageUrl).openStream().use { stream ->
                BitmapFactory.decodeStream(stream, null, options)
            }

            bitmap?.let {
                launch { memoryCache?.putImage(article.imageKey, it) }
                launch { diskCache?.put(article.imageKey, it) }
                Log.d("downloadImage", "Image downloaded and cached")
            }

            bitmap
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
}

/**
 * Downloads the full-size image from the given URL.
 *
 * This function is used when we want to download the full-size image to display on details page.
 */
suspend fun downloadFullSizeImage(imageUrl: String): Bitmap? {
    return withContext(Dispatchers.IO) {
        try {

            Log.d("downloadImage", "Downloading full sized image")
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