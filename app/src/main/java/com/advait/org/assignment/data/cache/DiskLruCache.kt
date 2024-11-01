package com.advait.org.assignment.data.cache

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.security.MessageDigest

class DiskLruCache(private val context: Context, private val maxSize: Long) {
    private val cacheDir: File = File(context.cacheDir, "ImageGridCache")
    private val ioScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init {
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }
    }

    private fun hashKeyForDisk(key: String): String {
        return MessageDigest.getInstance("MD5").digest(key.toByteArray()).joinToString("") {
            "%02x".format(it)
        }
    }

    // Changed to suspend function for better coroutine integration
    suspend fun put(key: String, bitmap: Bitmap) = withContext(ioScope.coroutineContext) {
        val file = File(cacheDir, hashKeyForDisk(key))
        if (file.exists()) {
            Log.d("DiskCache", "Image already exists in disk cache with key: $key")
            return@withContext
        }

        try {
            // Use lower quality (85) for faster saving and smaller file size
            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
            }
            // Move eviction to a separate coroutine to not block the saving
            launch { evictOldFilesIfNeeded() }
            Log.d("DiskCache", "Image cached to disk with key: ${hashKeyForDisk(key)}")
        } catch (e: IOException) {
            Log.e("DiskCache", "Failed to cache image to disk", e)
        }
    }

    // Changed to suspend function and added optimization options
    suspend fun get(key: String): Bitmap? = withContext(Dispatchers.IO) {
        val file = File(cacheDir, hashKeyForDisk(key))
        if (!file.exists()) {
            Log.d("DiskCache", "Image not found in disk cache with key: $key")
            return@withContext null
        }

        try {
            // Use RGB_565 for thumbnails to reduce memory usage
            val options = BitmapFactory.Options().apply {
                inPreferredConfig = Bitmap.Config.RGB_565
            }
            BitmapFactory.decodeFile(file.absolutePath, options)
        } catch (e: Exception) {
            Log.e("DiskCache", "Failed to read image from disk", e)
            null
        }
    }

    suspend fun clearCache() = withContext(Dispatchers.IO) {
        cacheDir.listFiles()?.forEach { it.delete() }
        Log.d("DiskCache", "Disk cache cleared")
    }

    private fun getCurrentCacheSize(): Long {
        return cacheDir.listFiles()?.sumOf { it.length() } ?: 0L
    }

    private suspend fun evictOldFilesIfNeeded() = withContext(Dispatchers.IO) {
        val files = cacheDir.listFiles()?.sortedBy { it.lastModified() } ?: return@withContext
        var currentSize = getCurrentCacheSize()

        for (file in files) {
            if (currentSize <= maxSize) break
            currentSize -= file.length()
            file.delete()
        }
    }
}