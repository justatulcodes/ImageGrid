package com.advait.org.assignment.presentation.stateholders

import android.graphics.Bitmap
import com.advait.org.assignment.data.cache.DiskLruCache
import com.advait.org.assignment.domain.model.Article
import com.advait.org.assignment.domain.LRUMemoryCache

data class ImageScreenState(

    val isInternetAvailable : Boolean = false,
    val isLoading : Boolean = false,
    val infoMessage : String = "",
    val errors : ImageScreenErrors? = null,

    val articleUrls : List<Article> = emptyList(),
    val selectedArticleIndex : Int = 0,
    val selectedBitmap : Bitmap? = null,

    val memoryCache : LRUMemoryCache? = null,
    val diskCache : DiskLruCache? = null

)