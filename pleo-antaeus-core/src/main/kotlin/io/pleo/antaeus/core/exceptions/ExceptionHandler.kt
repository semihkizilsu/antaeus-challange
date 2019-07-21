package io.pleo.antaeus.core.exceptions

sealed class ExceptionHandler {
    data class Success<T: Any>(val value: T) : ExceptionHandler()
    data class Error(val cause: Exception) : ExceptionHandler()
}