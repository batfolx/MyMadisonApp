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

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import kotlinx.coroutines.Deferred
import retrofit2.Response

fun <Data, ResultType, RequestType> getResource(
    loadFromDb: (data: Data) -> LiveData<ResultType>,
    createCall: (data: Data) -> Deferred<Response<RequestType>>,
    requestToResult: (item: RequestType) -> ResultType,
    saveResult: suspend (item: ResultType) -> Long,
    shouldFetch: suspend (data: ResultType?) -> Boolean = { true },
    onFetchFailed: () -> Unit = {},
    data: Data
): LiveData<Result<ResultType>> = liveData {
    val dbSource = loadFromDb(data)
    val disposable = emitSource(dbSource.map {
        Result.loading(it)
    })
    try {
        val index = if (shouldFetch(dbSource.value)) {
            createCall(data).await().let { response ->
                if (response.isSuccessful && response.body() != null) {
                    val result = requestToResult(response.body()!!)
                    disposable.dispose()

                    saveResult(result)
                } else throw ResponseException(response)
            }
        } else -1
        emitSource(loadFromDb(data).map {
            Result.success(it, index)
        })
    } catch (exception: Exception) {
        emitSource(loadFromDb(data).map {
            Result.error(exception, it)
        })
        onFetchFailed()
    }
}

fun <Data, ResultType> getResource(
    loadFromDb: (data: Data) -> LiveData<ResultType>,
    createCall: (data: Data) -> Deferred<Response<ResultType>>,
    saveResult: suspend (item: ResultType) -> Long,
    shouldFetch: suspend (data: ResultType?) -> Boolean = { true },
    onFetchFailed: () -> Unit = {},
    data: Data
): LiveData<Result<ResultType>> =
    getResource(
        loadFromDb,
        createCall,
        { item: ResultType -> item },
        saveResult,
        shouldFetch,
        onFetchFailed,
        data
    )

class ResponseException(val response: Response<*>) : Exception(response.errorBody()?.string() ?: "Response code: ${response.code()}")