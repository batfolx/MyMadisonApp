package com.jmu.mymadisonapp

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Environment.getExternalStorageDirectory
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.File
import kotlin.reflect.KClass


val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
/*fun createGson(typeAdapters: Map<Type, Any> = emptyMap()): Gson = GsonBuilder()
    .setPrettyPrinting()
    .apply { typeAdapters.forEach { registerTypeAdapter(it.key, it.value) } }
    .create()*/

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


fun isAppInstalled(pkg: String = "", manager: PackageManager): Boolean {
    return try {
        manager.getPackageInfo(pkg, 0)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
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

fun logMsg(tag: String, msg: String, splitLong: Boolean, logger: (String, String) -> Int): Unit =
    if (splitLong) splitMessage(msg).forEach { logger(tag, it) }
    else { logger(tag, msg); Unit }

fun Any.log(tag: String = getSimpleName(), msg: String = "Empty", splitLong: Boolean = true) = logMsg(tag, msg, splitLong, Log::v)

fun Any.logD(tag: String = getSimpleName(), msg: String = "Empty", splitLong: Boolean = true) = logMsg(tag, msg, splitLong, Log::d)

fun Any.logE(tag: String = getSimpleName(), msg: String = "Empty", splitLong: Boolean = true) = logMsg(tag, msg, splitLong, Log::e)

val log = Any::log
val logD = Any::logD
val logE = Any::logE

/*suspend fun tryCatch(
    tryBlock: suspend CoroutineScope.() -> Unit,
    catchBlock: suspend CoroutineScope.(Throwable) -> Unit
) {
    coroutineScope {
        try {
            tryBlock()
        } catch (e: Throwable) {
            if (e !is CancellationException) catchBlock(e)
        }
    }
}*/

/*suspend fun CoroutineScope.launchOnUI(block: suspend CoroutineScope.() -> Unit) =
    coroutineScope { launch(Dispatchers.Main) { block() } }*/
infix fun Float.digits(digits: Int) = "%.${digits}f".format(this)
infix fun Double.digits(digits: Int) = "%.${digits}f".format(this)

infix fun Int.zeros(zeros: Int) = "%0${zeros}d".format(this)
infix fun Long.zeros(zeros: Int) = "%0${zeros}d".format(this)
infix fun Float.zeros(zeros: Int) = "%0${zeros}f".format(this)
infix fun Double.zeros(zeros: Int) = "%0${zeros}f".format(this)

inline fun <T> Boolean.ifDo(block: () -> T, orElse: () -> T) = if (this) block() else orElse()
inline fun Boolean.ifDo(block: () -> Unit) = if (this) block() else Unit

val tempDir: File by lazy {
    File(getExternalStorageDirectory(), "/openPH/openPH_cache.json")
        .also { file -> file.takeUnless { it.exists() }?.createNewFile() }
}
