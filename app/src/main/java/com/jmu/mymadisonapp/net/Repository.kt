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
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import kotlinx.coroutines.Deferred
import retrofit2.Response

/*
fun <Data, ResultType, RequestType> getResource(
    loadFromDb: (data: Data) -> LiveData<ResultType?>,
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
            it?.let { Result.success(it, index) } ?: Result.error(NullPointerException("Database Load returned null"), it)
        })
    } catch (exception: Exception) {
        exception.printStackTrace()
        emitSource(loadFromDb(data).map {
            Result.error(exception, it)
        })
        onFetchFailed()
    }
}

fun <Data, ResultType> getResource(
    loadFromDb: (data: Data) -> LiveData<ResultType?>,
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

fun <ResultType, RequestType> getResource(
    loadFromDb: () -> LiveData<ResultType?>,
    createCall: () -> Deferred<Response<RequestType>>,
    requestToResult: (item: RequestType) -> ResultType,
    saveResult: suspend (item: ResultType) -> Long,
    shouldFetch: suspend (data: ResultType?) -> Boolean = { true },
    onFetchFailed: () -> Unit = {}
): LiveData<Result<ResultType>> =
    getResource<Any?, ResultType, RequestType>(
        { loadFromDb() },
        { createCall() },
        requestToResult,
        saveResult,
        shouldFetch,
        onFetchFailed,
        null)

fun <ResultType> getResource(
    loadFromDb: () -> LiveData<ResultType?>,
    createCall: () -> Deferred<Response<ResultType>>,
    saveResult: suspend (item: ResultType) -> List<Long>,
    shouldFetch: suspend (data: ResultType?) -> Boolean = { true },
    onFetchFailed: () -> Unit = {}
): LiveData<Result<ResultType>> =
    getResource<Any?, ResultType, ResultType>(
        { loadFromDb() },
        { createCall() },
        { item: ResultType -> item },
        { saveResult(it).firstOrNull() ?: -1 },
        shouldFetch,
        onFetchFailed,
        null)*/

fun <ResultType> singleResource(block: ResourceBuilder<ResultType, ResultType>.() -> Unit): LiveData<Result<ResultType>> =
    resource(block)

fun <ResultType, RequestType> resource(block: ResourceBuilder<ResultType, RequestType>.() -> Unit): LiveData<Result<ResultType>> =
    ResourceBuilder(block).perform()

class ResourceBuilder<ResultType, RequestType>(request: ResourceBuilder<ResultType, RequestType>.() -> Unit) {
    private lateinit var _loadFromDb: () -> LiveData<ResultType?>
    private lateinit var _createCall: () -> Deferred<Response<RequestType>>
    private lateinit var _saveResult: suspend (item: ResultType) -> Long
    private var _shouldFetch: (data: ResultType?) -> Boolean = { true }
    private var _onFetchFailed: () -> Unit = {}
    private lateinit var _convertRequest: (item: RequestType) -> ResultType

    fun loadFromDb(block: () -> LiveData<ResultType?>) {
        _loadFromDb = block
    }

    fun createCall(block: () -> Deferred<Response<RequestType>>) {
        _createCall = block
    }

    fun saveResult(block: suspend (item: ResultType) -> Long) {
        _saveResult = block
    }

    fun shouldFetch(block: (data: ResultType?) -> Boolean) {
        _shouldFetch = block
    }

    fun onFailed(block: () -> Unit) {
        _onFetchFailed = block
    }

    fun convertRequest(block: (item: RequestType) -> ResultType) {
        _convertRequest = block
    }

    fun perform() = liveData<Result<ResultType>> {
        val dbSource = if (::_loadFromDb.isInitialized) _loadFromDb() else MutableLiveData()
        val disposable = emitSource(dbSource.map {
            Result.loading(it)
        })
        try {
            val index = if (_shouldFetch(dbSource.value) && ::_createCall.isInitialized) {
                _createCall().await().let { response ->
                    if (response.isValid()) {
                        val result =
                            if (::_convertRequest.isInitialized) _convertRequest(response.body()!!) else response.body()!! as ResultType
                        disposable.dispose()

                        if (::_saveResult.isInitialized) _saveResult(result) else -1
                    } else throw ResponseException(response)
                }
            } else -1
            emitSource(
                (if (::_loadFromDb.isInitialized) _loadFromDb() else MutableLiveData())
                    .map {
                        it?.let { result -> Result.success(result, index) }
                            ?: Result.error(NullPointerException("Database Load returned null"), it)
                    }
            )
        } catch (exception: Exception) {
            exception.printStackTrace()
            emitSource(
                (if (::_loadFromDb.isInitialized) _loadFromDb() else MutableLiveData())
                    .map { Result.error(exception, it) }
            )
            _onFetchFailed()
        }
    }

    init {
        request()
    }

}

class ResponseException(response: Response<*>) :
    Exception(response.errorBody()?.string() ?: "Response code: ${response.code()}")