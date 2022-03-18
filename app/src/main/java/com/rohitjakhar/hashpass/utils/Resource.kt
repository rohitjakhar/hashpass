package com.rohitjakhar.hashpass.utils

sealed class Resource<T>(
    open val data: T? = null,
    open val message: String = "",
    open val errorType: ErrorType = ErrorType.UNKNOWN
) {
    class Loading<T>() : Resource<T>()

    data class Sucess<T>(override val data: T?, override val message: String = "") : Resource<T>()

    data class Error<T>(
        override val errorType: ErrorType = ErrorType.UNKNOWN,
        override val message: String = errorType.errorMessage
    ) : Resource<T>()
}

enum class ErrorType(val errorMessage: String) {
    UNKNOWN(errorMessage = "Error"),
    EMPTY_DATA("Empty Data")
}