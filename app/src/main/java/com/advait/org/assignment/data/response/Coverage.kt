package com.advait.org.assignment.data.response

import kotlinx.serialization.Serializable

@Serializable
data class Coverage(
    val id: String,
    val title: String,
    val language: String,
    val thumbnail: Thumbnail,
    val mediaType: Int,
    val coverageURL: String,
    val publishedAt: String,
    val publishedBy: String,
    val backupDetails: BackupDetails?= null,
    val description: String,
    val seoSlugWithId: String
)

@Serializable
data class Thumbnail(
    val id: String,
    val version: Int,
    val domain: String,
    val basePath: String,
    val key: String,
    val qualities: List<Int>,
    val aspectRatio: Float
)

@Serializable
data class BackupDetails(
    val pdfLink: String,
    val screenshotURL: String
)
