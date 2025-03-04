package com.saksham.modulemanager.util

/**
 * A generic class that holds a value or an error
 */
sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val message: String, val exception: Exception? = null) : Result<Nothing>()
    object Loading : Result<Nothing>()
    
    /**
     * Returns true if this is a Success
     */
    val isSuccess: Boolean
        get() = this is Success
    
    /**
     * Returns true if this is an Error
     */
    val isError: Boolean
        get() = this is Error
    
    /**
     * Returns true if this is Loading
     */
    val isLoading: Boolean
        get() = this is Loading
    
    /**
     * Returns the data if this is a Success, null otherwise
     */
    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }
    
    /**
     * Returns the error message if this is an Error, null otherwise
     */
    fun errorOrNull(): String? = when (this) {
        is Error -> message
        else -> null
    }
}
