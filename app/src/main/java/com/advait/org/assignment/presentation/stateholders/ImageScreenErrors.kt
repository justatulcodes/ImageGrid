package com.advait.org.assignment.presentation.stateholders

sealed class ImageScreenErrors {
    data class GenericError(val message: String) : ImageScreenErrors()
    data class NetworkFailure(val message: String) : ImageScreenErrors()
    data class HttpError(val statusCode: Int, val message: String) : ImageScreenErrors()

}