package com.advait.org.assignment.data.cache

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.advait.org.assignment.data.network.downloadFullSizeImage
import com.advait.org.assignment.data.network.downloadThumbnail
import com.advait.org.assignment.domain.LRUMemoryCache
import com.advait.org.assignment.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException

class ImageLoader private constructor(
    private val applicationContext: Context,
    private val memoryCache: LRUMemoryCache,
    private val diskCache: DiskLruCache
) {
    companion object {
        @Volatile
        private var instance: ImageLoader? = null

        fun getInstance(
            context: Context,
            memoryCacheSize: Int = Config.memoryCacheSize,
            diskCacheSize: Long = Config.diskCacheSize
        ): ImageLoader {
            return instance ?: synchronized(this) {
                instance ?: ImageLoader(
                    context.applicationContext,
                    MemoryLruCache(memoryCacheSize),
                    DiskLruCache(context, diskCacheSize)
                ).also { instance = it }
            }
        }
    }

    /**
     * Load image with caching strategy for thumbnails
     */
    suspend fun loadImage(
        imageUrl: String,
        imageKey: String? = null,
    ): ImageLoadResult {
        val key = imageKey ?: imageUrl

        try {
            memoryCache.getImage(key)?.let { bitmap ->
                return ImageLoadResult.Success(bitmap)
            }

            diskCache.get(key)?.let { bitmap ->
                memoryCache.putImage(key, bitmap)
                return ImageLoadResult.Success(bitmap)
            }

            val bitmap = downloadThumbnail(
                imageUrl,
                Constants.THUMBNAIL_IMG_WIDTH,
                Constants.THUMBNAIL_IMG_HEIGHT
            )

            return if (bitmap != null) {
                coroutineScope {
                    launch { memoryCache.putImage(key, bitmap) }
                    launch { diskCache.put(key, bitmap) }
                }
                ImageLoadResult.Success(bitmap)
            } else {
                ImageLoadResult.Error("Failed to download image")
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return ImageLoadResult.Error(e.message ?: "Unknown error")
        }
    }

    /**
     * Load full-sized image with memory-only caching
     */
    suspend fun loadFullSizedImage(
        imageUrl: String,
        imageKey: String? = null
    ): Bitmap? {
        val key = imageKey ?: imageUrl

        try {
            memoryCache.getImage(key)?.let { return it }

            val bitmap = downloadFullSizeImage(imageUrl)

            bitmap?.let { memoryCache.putImage(key, it) }

            return bitmap
        } catch (e: Exception) {
            Log.e("ImageLoader", "Error loading full size image: ${e.message}", e)
            return null
        }
    }

    sealed class ImageLoadResult {
        data class Success(val bitmap: Bitmap) : ImageLoadResult()
        data class Error(val message: String) : ImageLoadResult()
    }

    fun clearCache() {
        memoryCache.clearCache()
        runBlocking {
            withContext(Dispatchers.IO) {
                diskCache.clearCache()
            }
        }
    }
}
