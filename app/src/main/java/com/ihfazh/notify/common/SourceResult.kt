package com.ihfazh.notify.common

sealed class SourceResult<T>{
    data class Success<T>(val data: T): SourceResult<T>()
    data class Error<T>(val message: String? = null, val throwable: Throwable? = null): SourceResult<T>()
}
