package com.advait.org.assignment.domain

import android.graphics.Bitmap

interface LRUMemoryCache {

    fun putImage(url: String, bitmap: Bitmap)
    fun getImage(url: String): Bitmap?
    fun clearCache()

}