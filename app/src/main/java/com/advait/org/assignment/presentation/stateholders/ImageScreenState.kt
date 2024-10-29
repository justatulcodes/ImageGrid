package com.advait.org.assignment.presentation.stateholders

data class ImageScreenState(
    val isLoading : Boolean = false,
    val imageUrls : List<String> = emptyList(),
    val infoMessage : String = "",
    val errors : ImageScreenErrors? = null
)