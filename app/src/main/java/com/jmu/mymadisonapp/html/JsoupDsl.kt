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

package com.jmu.mymadisonapp.html

import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.util.*

@DslMarker
annotation class Select

@Select
open class AbstractSelector {
    private val selectors: MutableList<AbstractSelector> = LinkedList()

    fun <S : AbstractSelector> initSelector(selector: S, init: S.() -> Unit): S {
        selectors += selector.apply(init)
        return selector
    }
}

open class HtmlAdapter(document: Document, selectors: AbstractSelector) : AbstractSelector()


fun select(responseBody: ResponseBody, url: String, init: AbstractSelector.() -> Unit): HtmlAdapter =
    HtmlAdapter(
        Jsoup.parse(responseBody.byteStream(), responseBody.contentType()?.charset()?.name(), url),
        AbstractSelector().apply(init)
    )

