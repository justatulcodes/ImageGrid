package com.advait.org.assignment.domain

import android.graphics.Bitmap

interface LruImageCache {

    fun cacheImage(url: String, bitmap: Bitmap)
    fun getImage(url: String): Bitmap?
    fun clearCache()

}