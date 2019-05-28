/*
 * Copyright 2019 Timothy Logan
 * Copyright 2019 Victor Velea
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jmu.mymadisonapp.net

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
        fun <T> success(data: T, index: Long = -1): Result<T> =
            Success(data, index)

        fun <T> error(exception: Exception, data: T?): Result<T> =
            Error(exception, data)

        fun <T> loading(data: T?): Result<T> = Loading(data)
    }
}

data class Success<out T>(override val data: T, val index: Long) : Result<T>(data)
data class Error<out T>(val exception: Exception, override val data: T?) : Result<T>(data)
data class Loading<out T>(override val data: T?) : Result<T>(data)