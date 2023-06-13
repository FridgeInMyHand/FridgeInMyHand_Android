package com.kykint.fridgeinmyhand.utils

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Looper
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import androidx.annotation.RequiresApi
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
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

val isMainThread
    get() = Looper.getMainLooper().isCurrentThread

// https://www.digitalocean.com/community/tutorials/android-capture-image-camera-gallery
fun Context.getGalleryAndCameraIntents(tempPicUri: Uri): Intent {
    val intents = arrayListOf<Intent>()

    // Camera intents
    val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    packageManager.queryIntentActivities(captureIntent, 0).map { res ->
        Intent(captureIntent).apply {
            component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
            setPackage(res.activityInfo.packageName)
            putExtra(MediaStore.EXTRA_OUTPUT, tempPicUri)
        }
    }.let { intents.addAll(it) }

    val galleryIntent = Intent(Intent.ACTION_GET_CONTENT).apply { type = "image/*" }
    packageManager.queryIntentActivities(galleryIntent, 0).map { res ->
        Intent(galleryIntent).apply {
            component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
            setPackage(res.activityInfo.packageName)
        }
    }.let { intents.addAll(it) }

    return if (intents.isEmpty()) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, tempPicUri)
        }
    } else
        Intent.createChooser(intents.last(), "Select source").apply {
            putExtra(Intent.EXTRA_INITIAL_INTENTS, intents.toTypedArray())
        }
}

// https://stackoverflow.com/a/71309183
@RequiresApi(Build.VERSION_CODES.O)
fun Context.createFileFromContentUri(fileUri: Uri, filePath: String): File? {
    var fileName: String? = null

    fileUri.let { returnUri ->
        contentResolver.query(returnUri, null, null, null)
    }?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        cursor.moveToFirst()
        fileName = cursor.getString(nameIndex)
    }

    if (fileName == null) {
        Log.e("createFileFromContentUri()", "Couldn't find file uri")
        return null
    }

    val outputFile = File(filePath)
    contentResolver.openInputStream(fileUri)!!.use { inputStream ->
        inputStream.copyTo(outputFile.outputStream())
    }

    return outputFile
}

fun getDaysFromTodayInTimestamp(days: Long): Long {
    val currentDate = LocalDate.now()
    val futureDate = currentDate.plusDays(days)
    val instant = futureDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
    return instant.epochSecond
}
