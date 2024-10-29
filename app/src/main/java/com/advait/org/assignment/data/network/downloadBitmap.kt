package com.advait.org.assignment.data.network

import android.content.ContentValues.TAG
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.URL

suspend fun downloadBitmap(imageUrl: String): Bitmap? {
    return withContext(Dispatchers.IO) {
        try {
            val url = URL(imageUrl)
            val conn = url.openConnection().apply {
                connectTimeout = 5000
                readTimeout = 5000
            }
            conn.getInputStream().use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network error: ${e.message}", e)
            null
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error: ${e.message}", e)
            null
        }
    }
}
