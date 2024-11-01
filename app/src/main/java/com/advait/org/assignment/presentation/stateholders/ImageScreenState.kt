package com.advait.org.assignment.presentation.stateholders

import android.graphics.Bitmap
import com.advait.org.assignment.data.cache.DiskLruCache
import com.advait.org.assignment.domain.Image
import com.advait.org.assignment.domain.LruImageCache

data class ImageScreenState(
    val isLoading : Boolean = false,
    val imageUrls : List<Image> = emptyList(),
    val isInternetAvailable : Boolean = false,
    val selectedImage: Image? = null,
    val infoMessage : String = "",
    val errors : ImageScreenErrors? = null,
    val selectedImageUrl : String = "",
    val selectedImageIndex : Int = -1,
    val selectedBitmap : Bitmap? = null,
    val memoryCache : LruImageCache? = null,
    val diskCache : DiskLruCache? = null
)