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

import kotlin.reflect.KMutableProperty0

inline fun <reified T : Any, reified S : Any> Builder<T>.simpleProperty(
    value: String,
    noinline property: T.() -> KMutableProperty0<S>,
    noinline stringConversion: String.() -> S
) =
    property(makeProperty(value, property).apply {
        defaultConverter(stringConversion)
    })

inline fun <reified T : Any> Builder<T>.intProperty(value: String, noinline property: T.() -> KMutableProperty0<Int>) =
    simpleProperty(value, property, String::toInt)

inline fun <reified T : Any> Builder<T>.longProperty(
    value: String,
    noinline property: T.() -> KMutableProperty0<Long>
) =
    simpleProperty(value, property, String::toLong)

inline fun <reified T : Any> Builder<T>.floatProperty(
    value: String,
    noinline property: T.() -> KMutableProperty0<Float>
) =
    simpleProperty(value, property, String::toFloat)

inline fun <reified T : Any> Builder<T>.doubleProperty(
    value: String,
    noinline property: T.() -> KMutableProperty0<Double>
) =
    simpleProperty(value, property, String::toDouble)

inline fun <reified T : Any> Builder<T>.stringProperty(
    value: String,
    noinline property: T.() -> KMutableProperty0<String>
) =
    simpleProperty(value, property, { this })