package com.advait.org.assignment.data.cache

import android.graphics.Bitmap
import androidx.collection.LruCache
import com.advait.org.assignment.domain.LRUMemoryCache

class MemoryLruCache(memoryCacheSize: Int) : LRUMemoryCache {

    private val cache : LruCache<String, Bitmap> = object : LruCache<String, Bitmap>(memoryCacheSize) {
        override fun sizeOf(key: String, value: Bitmap): Int {
            return (value.rowBytes)*(value.height)/1024
        }
    }

    override fun putImage(url: String, bitmap: Bitmap) {
        cache.put(url,bitmap)
    }

    override fun getImage(url: String): Bitmap? {
        return cache[url]
    }

    override fun clearCache() {
        cache.evictAll()
    }
}