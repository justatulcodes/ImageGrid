package com.advait.org.assignment.presentation.stateholders

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advait.org.assignment.data.cache.Config
import com.advait.org.assignment.data.cache.DiskLruCache
import com.advait.org.assignment.data.cache.MemoryLruCache
import com.advait.org.assignment.data.network.fetchMediaCoverages
import com.advait.org.assignment.data.response.Coverage
import com.advait.org.assignment.domain.model.Article
import com.advait.org.assignment.utils.ConnectionState
import com.advait.org.assignment.utils.ConnectivityObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val connectivityObserver: ConnectivityObserver
): ViewModel() {

    private val _uiState = MutableStateFlow(ImageScreenState())
    val uiState: StateFlow<ImageScreenState> get() = _uiState


    init {
        val imageCache = MemoryLruCache(Config.memoryCacheSize)
        _uiState.value = _uiState.value.copy(memoryCache = imageCache)

        observeConnectivity()
    }

    private fun observeConnectivity() {
        connectivityObserver.connectionState
            .distinctUntilChanged()
            .map { it === ConnectionState.Available }
            .onEach { isConnected ->
                _uiState.update { it.copy(isInternetAvailable = isConnected) }
            }
            .launchIn(viewModelScope)
    }


    fun setupDiskCache(diskCache: DiskLruCache) {
        _uiState.value = _uiState.value.copy(diskCache = diskCache)
    }


    fun fetchMediaCoveragesData() {
        viewModelScope.launch {

            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                withContext(Dispatchers.IO) {
                    val mediaCoverages = fetchMediaCoverages()
                    if(!mediaCoverages.isNullOrEmpty()) {
                        val imageUrls = mediaCoverages.map { constructImageUrl(it) }

                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            articleUrls = imageUrls,
                            errors = null
                        )
                    }else{
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errors = ImageScreenErrors.GenericError("No media coverages found.")
                        )
                    }
                }


            }catch (e: IOException) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errors = ImageScreenErrors.NetworkFailure("Network connection error.")
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errors = ImageScreenErrors.GenericError("An unexpected error occurred.")
                )
            }
        }
    }

    private fun constructImageUrl(coverage: Coverage): Article {
        val thumbnail = coverage.thumbnail
        val imageUrl = "${thumbnail.domain}/${thumbnail.basePath}/0/${thumbnail.key}"

        val article = Article(
            imageUrl = imageUrl,
            imageKey = thumbnail.basePath,
            title = coverage.title,
            description = coverage.description,
            publishedOn = coverage.publishedAt,
            publishedBy = coverage.publishedBy,
            articleUrl = coverage.coverageURL
            )

        return article
    }

    fun setSelectedArticle(article: Article) {
        val index = _uiState.value.articleUrls.indexOfFirst { it.imageUrl == article.imageUrl }
        _uiState.value = _uiState.value.copy(selectedArticleIndex = index)
    }

    fun setSelectedArticleIndex(index: Int) {
        _uiState.value = _uiState.value.copy(selectedArticleIndex = index)
    }

    fun setSelectedBitmap(bitmap: Bitmap) {
        _uiState.value = _uiState.value.copy(selectedBitmap = bitmap)
    }

    fun clearSelectedBitmap() {
        _uiState.value = _uiState.value.copy(selectedBitmap = null)
    }

}