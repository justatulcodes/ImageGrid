package com.advait.org.assignment.data.cache

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.advait.org.assignment.domain.LRUDiskCache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.security.MessageDigest

class DiskLruCache(private val context: Context, private val maxSize: Long) : LRUDiskCache {
    private val cacheDir: File = File(context.cacheDir, Config.CACHE_DIR)
    private val ioScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init {
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }
    }

    override suspend fun put(key: String, bitmap: Bitmap) = withContext(ioScope.coroutineContext) {
        val file = File(cacheDir, hashKeyForDisk(key))
        if (file.exists()) {
            return@withContext
        }

        try {
            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
            }
            launch { evictOldFilesIfNeeded() }
        } catch (e: IOException) {
            Log.e("DiskCache", "Failed to cache image to disk", e)
        }
    }

    private fun hashKeyForDisk(key: String): String {
        return MessageDigest.getInstance("MD5").digest(key.toByteArray()).joinToString("") {
            "%02x".format(it)
        }
    }

    override suspend fun get(key: String): Bitmap? = withContext(Dispatchers.IO) {
        val file = File(cacheDir, hashKeyForDisk(key))
        if (!file.exists()) {
            return@withContext null
        }
        try {
            val options = BitmapFactory.Options().apply {
                inPreferredConfig = Bitmap.Config.RGB_565
            }
            BitmapFactory.decodeFile(file.absolutePath, options)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun clearCache(): Unit = withContext(Dispatchers.IO) {
        cacheDir.listFiles()?.forEach { it.delete() }
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