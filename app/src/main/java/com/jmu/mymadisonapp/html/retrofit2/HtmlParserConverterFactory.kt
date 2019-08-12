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

package com.jmu.mymadisonapp.html.retrofit2

import com.jmu.mymadisonapp.html.Builder
import com.jmu.mymadisonapp.html.Parseable
import okhttp3.HttpUrl
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class HtmlParserConverterFactory : Converter.Factory() {

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? =
        (annotations.find { it is Parseable } as? Parseable)
            ?.objClass?.objectInstance?.let { HtmlParserConverter(it.build(), retrofit.baseUrl()) }
}

class HtmlParserConverter<T : Any>(private val builder: Builder<T>, private val httpUrl: HttpUrl) :
    Converter<ResponseBody, T> {

    override fun convert(value: ResponseBody): T? =
        builder.make(value.byteStream(), value.contentType()?.charset()?.name(), httpUrl.uri().toString())
}