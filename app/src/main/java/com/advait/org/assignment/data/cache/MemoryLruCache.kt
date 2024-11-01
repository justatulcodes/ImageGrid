package com.advait.org.assignment.data.cache

import android.graphics.Bitmap
import android.util.Log
import androidx.collection.LruCache
import com.advait.org.assignment.domain.LruImageCache

class MemoryLruCache(newMaxSize: Int) : LruImageCache {
    private val cache : LruCache<String, Bitmap>
    init {
        val cacheSize : Int
        if (newMaxSize > Config.maxMemory) {
            cacheSize = Config.memoryCacheSize
            Log.d("memory_cache","New value of cache is bigger than maximum cache available on system")
        } else {
            cacheSize = newMaxSize
            Log.d("memory_cache","maxMemory in MB : ${Config.maxMemory/1024} and newMaxSize in MB : ${newMaxSize.div(1024)}")
        }
        cache = object : LruCache<String, Bitmap>(cacheSize) {
            override fun sizeOf(key: String, value: Bitmap): Int {
                return (value.rowBytes)*(value.height)/1024
            }
        }
    }

    override fun cacheImage(url: String, bitmap: Bitmap) {
        cache.put(url,bitmap)
    }

    override fun getImage(url: String): Bitmap? {
        return cache[url]
    }

    override fun clearCache() {
        cache.evictAll()
    }
}