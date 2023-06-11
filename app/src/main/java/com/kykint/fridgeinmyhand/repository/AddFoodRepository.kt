package com.kykint.fridgeinmyhand.repository

import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.WorkerThread
import com.kykint.fridgeinmyhand.api.AiApi
import java.io.File

interface IAddFoodRepository {
    @WorkerThread
    suspend fun getFoodNamesFromImage(
        bitmap: Bitmap,
        onSuccess: (List<String>) -> Unit = {},
        onFailure: () -> Unit = {},
    )

    @WorkerThread
    suspend fun getFoodNamesFromImage(
        filePath: String,
        onSuccess: (List<String>) -> Unit = {},
        onFailure: () -> Unit = {},
    )

    @WorkerThread
    suspend fun getFoodNamesFromImage(
        file: File,
        onSuccess: (List<String>) -> Unit = {},
        onFailure: () -> Unit = {},
    )
}

/**
 * Used for testing before backend implementation is done
 */
class AddFoodRepositoryImpl : IAddFoodRepository {
    @RequiresApi(Build.VERSION_CODES.O)
    @WorkerThread
    override suspend fun getFoodNamesFromImage(
        bitmap: Bitmap,
        onSuccess: (List<String>) -> Unit,
        onFailure: () -> Unit,
    ) {
        AiApi.getFoodLabels(bitmap, onSuccess, onFailure)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @WorkerThread
    override suspend fun getFoodNamesFromImage(
        filePath: String,
        onSuccess: (List<String>) -> Unit,
        onFailure: () -> Unit,
    ) {
        AiApi.getFoodLabels(filePath, onSuccess, onFailure)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @WorkerThread
    override suspend fun getFoodNamesFromImage(
        file: File,
        onSuccess: (List<String>) -> Unit,
        onFailure: () -> Unit,
    ) {
        AiApi.getFoodLabels(file, onSuccess, onFailure)
    }
}