package com.kykint.composestudy.utils

import android.content.Context
import android.graphics.Bitmap
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

fun <T> Array<T>.encode(): String {
    return joinToString(
        prefix = "[",
        separator = " ; ",
        postfix = "]",
    )
}

fun <T> Collection<T>.encode(): String {
    return joinToString(
        prefix = "[ ",
        separator = " ; ",
        postfix = " ]",
    )
}

// TODO: REMOVEME
fun Context.writeImageToSdcard() {
    val outPath = filesDir.path + "/logo_small.jpg"
    if (!File(outPath).isFile) {
        assets.open("logo_small.jpg").use {
            it.copyTo(FileOutputStream(outPath))
        }
    }
}

fun epochSecondsToSimpleDate(seconds: Long): String {
    return LocalDateTime.ofInstant(
        Instant.ofEpochSecond(seconds),
        ZoneId.systemDefault()
    ).format(
        DateTimeFormatter.ofPattern("yyyy-MM-dd")
    ).toString()
}

fun Bitmap.saveAsFile(outputPath: String): File? {
    //create a file to write bitmap data
    return try {
        val file = File(outputPath)
        file.createNewFile()

        val bitmapData: ByteArray
        ByteArrayOutputStream().use {
            this.compress(Bitmap.CompressFormat.PNG, 0, it)
            bitmapData = it.toByteArray()
        }

        FileOutputStream(file).use {
            it.write(bitmapData)
        }
        file
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
