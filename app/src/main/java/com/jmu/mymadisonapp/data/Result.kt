package com.timmahh.openph.data

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
sealed class Result<out T>(open val data: T?) {

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception, data=$data]"
            is Loading -> "Loading[data=$data]"
        }
    }

    companion object {
        fun <T> success(data: T, index: Long = -1): Result<T> = Success(data, index)

        fun <T> error(exception: Exception, data: T?): Result<T> = Error(exception, data)

        fun <T> loading(data: T?): Result<T> = Loading(data)
    }
}

data class Success<out T>(override val data: T, val index: Long) : Result<T>(data)
data class Error<out T>(val exception: Exception, override val data: T?) : Result<T>(data)
data class Loading<out T>(override val data: T?) : Result<T>(data)