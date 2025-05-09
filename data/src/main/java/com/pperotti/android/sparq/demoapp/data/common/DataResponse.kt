package com.pperotti.android.sparq.demoapp.data.common

/**
 * Generic Response Wrapper that can be reused by more than one repo.
 */
sealed class DataResponse<T> {

    /**
     * Success response with data
     * @param result
     * @return DataResponse
     */
    data class Success<T>(val result: T) : DataResponse<T>()

    /**
     * Error response with message and cause
     *
     * @param message
     * @param cause
     * @return DataResponse
     */
    data class Error<T>(val message: String?, val cause: Throwable?) : DataResponse<T>()
}
