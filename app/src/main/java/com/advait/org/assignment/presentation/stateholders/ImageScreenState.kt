package com.advait.org.assignment.presentation.stateholders

import com.advait.org.assignment.domain.model.Article

data class ImageScreenState(

    val isInternetAvailable : Boolean = false,
    val isLoading : Boolean = false,
    val infoMessage : String = "",
    val errors : ImageScreenErrors? = null,

    val articleUrls : List<Article> = emptyList(),
    val selectedArticleIndex : Int = 0,

)