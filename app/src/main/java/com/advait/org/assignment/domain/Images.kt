package com.advait.org.assignment.domain

data class Image(
    val imageKey : String,
    val title : String,
    val description : String,
    val publishedOn : String,
    val publishedBy : String,
    val imageUrl : String,
    val articleUrl : String
)