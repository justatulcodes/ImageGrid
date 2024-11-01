package com.advait.org.assignment.domain

import android.graphics.Bitmap

interface LRUDiskCache {

    suspend fun put(key: String, bitmap: Bitmap)
    suspend fun get(key: String): Bitmap?
    suspend fun clearCache()

}