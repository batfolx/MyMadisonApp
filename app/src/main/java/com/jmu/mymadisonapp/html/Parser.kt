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

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.io.InputStream
import java.util.*
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.full.createInstance

@DslMarker
annotation class RootMarker

@DslMarker
annotation class PropertyMarker


inline fun <reified T : Any> selector(value: String = "", noinline block: Builder<T>.() -> Unit) =
    Builder(value, T::class.createInstance()).apply(block)

@RootMarker
open class Builder<K : Any>(private val value: String = "") {
    private lateinit var _instance: K
    protected val selectors: MutableList<PropertySelector<*>> = LinkedList()

    constructor(value: String, instance: K) : this(value) {
        _instance = instance
    }

    fun <S : Any> property(
        value: String,
        reference: K.() -> KMutableProperty0<S>,
        block: PropertySelector<S>.() -> Unit
    ) =
        property(makeProperty(value, reference).apply(block))

//    fun <I : Any, S : Collection<I>> collection(value: String, reference: K.() -> KMutableProperty0<S>, block: CollectionSelector<I, S>.() -> Unit) =
//            property(makeListProperty(value, reference).apply(block))

    fun <S : Any> property(prop: PropertySelector<S>): PropertySelector<S> {
        selectors += (prop); return prop
    }

    fun <S : Any> makeProperty(value: String, reference: K.() -> KMutableProperty0<S>): PropertySelector<S> =
        PropertySelector(value, reference(_instance))

//    fun <I : Any, S : Collection<I>> makeListProperty(value: String, reference: K.() -> KMutableProperty0<S>): CollectionSelector<I, S> =
//            CollectionSelector(value, reference(_instance))

    fun make(inputStream: InputStream, charset: String?, baseUri: String) =
        Jsoup.parse(
            inputStream,
            charset,
            baseUri
        )?.run { if (value.isNotEmpty()) select(value) else allElements }?.let { parse(it) }

    protected fun parse(elements: Elements): K? =
        selectors.forEach { it.setProp(elements) }.let { _instance }

    protected fun parse(element: Element): K? =
        selectors.forEach { it.setProp(element) }.let { _instance }


}

inline fun <reified T : Any, reified S : Any> Builder<T>.propertySelector(
    value: String,
    noinline reference: T.() -> KMutableProperty0<S>,
    block: PropertySelector<S>.() -> Unit
) =
    makeProperty(value, reference).apply(block)

@PropertyMarker
open class PropertySelector<S : Any>(
    val value: String,
    private val propReference: KMutableProperty0<S>
) : Builder<S>(value, propReference.get()) {
    private var _attr: (Element) -> String = { element: Element -> element.text() }
    private var _index: Int = 0
    private lateinit var _defaultConverter: String.() -> S

    fun attr(block: (element: Element) -> String) {
        _attr = block
    }

    fun index(index: Int) {
        _index = index
    }

    fun index(block: () -> Int) = index(block())

    fun defaultConverter(block: String.() -> S) {
        _defaultConverter = block
    }

    fun setProp(element: Element) = setProp(element.select(value))

    open fun setProp(element: Elements): S? =
        with(element.select(value)) {
            if (selectors.isEmpty())
                getOrNull(_index)?.let(_attr)?.takeIf { this@PropertySelector::_defaultConverter.isInitialized }
                    ?._defaultConverter()?.let { propReference.set(it); it }
            else parse(this)
        }
}

/*inline fun <reified T : Any, reified I : Any, reified S : Collection<I>> Builder<T>.collectionSelector(value: String, noinline reference: T.() -> KMutableProperty0<S>, block: CollectionSelector<I, S>.() -> Unit) =
        makeListProperty(value, reference).apply(block)

class CollectionSelector<I : Any, S : Collection<I>>(value: String, private val propReference: KMutableProperty0<S>) : PropertySelector<S>(value, propReference) {
    private lateinit var _listMapper: List<I>.() -> S
    private lateinit var _listItemMapper: PropertySelector<I>

    fun collectionMapper(mapper: List<I>.() -> S) { _listMapper = mapper }
    fun itemMapper(mapper: PropertySelector<I>) { _listItemMapper = mapper }
    fun itemMapper(value: String, mapper: PropertySelector<I>.() -> Unit) = itemMapper(PropertySelector(value, ).apply(mapper))

    override fun setProp(element: Elements): S? =
        if (this@CollectionSelector::_listMapper.isInitialized) { propReference.set(element.select(value).mapNotNull { _listItemMapper.setProp(it) }._listMapper()); propReference.get() }
        else super.setProp(element)
}*/

interface Adapter<K : Any> {
    fun build(): Builder<K>
}