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

package com.jmu.mymadisonapp

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlin.reflect.KClass




val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

inline fun <reified T : Any?> T.toJson(): String =
    this?.let { moshi.adapter(T::class.java).indent("    ").toJson(this) } ?: "null value"

fun Any?.nil(): Boolean = this == null
fun Any?.notNull(): Boolean = !nil()
val nil = Any?::nil
val notNull = Any?::notNull

fun Collection<Any>.empty(): Boolean = isEmpty()
fun Collection<Any>.notEmpty(): Boolean = isNotEmpty()
val empty = Collection<Any>::empty
val notEmpty = Collection<Any>::notEmpty

fun Collection<Any?>.nullEmpty(): Boolean = isNullOrEmpty()
fun Collection<Any?>.notNullOrEmpty(): Boolean = !nullEmpty()
val nullEmpty = Collection<Any?>::nullEmpty
val notNullOrEmpty = Collection<Any?>::notNullOrEmpty


fun PackageManager.isAppInstalled(pkg: String = ""): Boolean =
    try {
        getPackageInfo(pkg, 0); true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }

fun Activity.checkPermissions(
    vararg permissions: String = arrayOf(Manifest.permission.INTERNET, Manifest.permission.WRITE_EXTERNAL_STORAGE),
    requestCode: Int = 300
) {
    permissions.filter { ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }
        .takeUnless { it.isEmpty() }
        ?.apply { ActivityCompat.requestPermissions(this@checkPermissions, this.toTypedArray(), requestCode) }
}

fun splitMessage(msg: String, maxSize: Int = 4000) =
    (0..(msg.length/maxSize)).map { msg.substring(it * maxSize,
        ((it+1) * maxSize).takeIf { end -> end <= msg.length } ?: msg.length) }

fun Any.getSimpleName(value: KClass<out Any>? = this::class): String =
    value?.java?.simpleName ?: "UnknownName"

inline fun logMsg(tag: String, msg: String, splitLong: Boolean, logger: (String, String) -> Int): Unit =
    if (splitLong) splitMessage(msg).forEach { logger(tag, it) }
    else { logger(tag, msg); Unit }

fun Any.log(tag: String = getSimpleName(), msg: String = "Empty", splitLong: Boolean = true) = logMsg(tag, msg, splitLong, Log::v)

fun Any.logD(tag: String = getSimpleName(), msg: String = "Empty", splitLong: Boolean = true) = logMsg(tag, msg, splitLong, Log::d)

fun Any.logE(tag: String = getSimpleName(), msg: String = "Empty", splitLong: Boolean = true) = logMsg(tag, msg, splitLong, Log::e)


/**
 * Helper to format a Float with a max [digits] number of digits after the decimal.
 */
infix fun Float.digits(digits: Int) = "%.${digits}f".format(this)

/**
 * Helper to format a Double with a max [digits] number of digits after the decimal.
 */
infix fun Double.digits(digits: Int) = "%.${digits}f".format(this)

/**
 * Helper to format a number with [zeros] leading zeros.
 */
infix fun Int.zeros(zeros: Int) = "%0${zeros}d".format(this)

/**
 * Helper to format a number with [zeros] leading zeros.
 */
infix fun Long.zeros(zeros: Int) = "%0${zeros}d".format(this)

/**
 * Helper to format a number with [zeros] leading zeros.
 */
infix fun Float.zeros(zeros: Int) = "%0${zeros}f".format(this)

/**
 * Helper to format a number with [zeros] leading zeros.
 */
infix fun Double.zeros(zeros: Int) = "%0${zeros}f".format(this)

/**
 * Helper to Boolean checks with an else block.
 */
inline fun <T> Boolean.ifDo(block: () -> T, orElse: () -> T) = if (this) block() else orElse()

/**
 * Helper to Boolean checks.
 */
inline fun Boolean.ifDo(block: () -> Unit) = ifDo(block) {}
