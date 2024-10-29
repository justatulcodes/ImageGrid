package com.advait.org.assignment.presentation.stateholders

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advait.org.assignment.data.network.fetchMediaCoverages
import com.advait.org.assignment.data.response.Thumbnail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class MainViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ImageScreenState())
    val uiState: StateFlow<ImageScreenState> get() = _uiState

    fun fetchMediaCoveragesData() {
        viewModelScope.launch {

            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                withContext(Dispatchers.IO) {
                    val mediaCoverages = fetchMediaCoverages()
                    if(!mediaCoverages.isNullOrEmpty()) {
                        val imageUrls = mediaCoverages.map { constructImageUrl(it.thumbnail) }

                        Log.d("ImageScreenViewModel", "Image URLs: $imageUrls")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            imageUrls = imageUrls,
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

    private fun constructImageUrl(thumbnail: Thumbnail): String {
        return "${thumbnail.domain}/${thumbnail.basePath}/0/${thumbnail.key}"
    }


}