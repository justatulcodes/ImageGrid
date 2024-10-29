package com.advait.org.assignment.data.network

import com.advait.org.assignment.data.response.Coverage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.net.URL

suspend fun fetchMediaCoverages(): List<Coverage>? {
    val urlString = "https://acharyaprashant.org/api/v2/content/misc/media-coverages?limit=100"
    val url = URL(urlString)
    
    return try {
        (withContext(Dispatchers.IO) { url.openConnection() } as? HttpURLConnection)?.

        run {
            requestMethod = "GET"
            connectTimeout = 10000
            readTimeout = 10000
            doInput = true

            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = inputStream.bufferedReader().use { it.readText() }
                Json.decodeFromString<List<Coverage>>(response)
            } else {
                null
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
