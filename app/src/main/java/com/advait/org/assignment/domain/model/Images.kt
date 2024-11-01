package com.advait.org.assignment.domain.model

/**
 * Represents an Article with its metadata
 */
data class Article(
    val imageKey : String,
    val title : String,
    val description : String,
    val publishedOn : String,
    val publishedBy : String,
    val imageUrl : String,
    val articleUrl : String
)