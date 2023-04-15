package com.kykint.composestudy.utils

import android.content.Context
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
    val outPath = filesDir.path+"/logo_small.jpg"
    if (!File(outPath).isFile) {
        assets.open("logo_small.jpg").use {
            it.copyTo(FileOutputStream(outPath))
        }
    }
}