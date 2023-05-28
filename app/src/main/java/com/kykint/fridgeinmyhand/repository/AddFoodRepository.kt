package com.kykint.fridgeinmyhand.repository

import android.graphics.Bitmap
import androidx.annotation.WorkerThread
import com.kykint.fridgeinmyhand.api.FridgeApi

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
}

/**
 * Used for testing before backend implementation is done
 */
class AddFoodRepositoryImpl : IAddFoodRepository {
    @WorkerThread
    override suspend fun getFoodNamesFromImage(
        bitmap: Bitmap,
        onSuccess: (List<String>) -> Unit,
        onFailure: () -> Unit,
    ) {
        FridgeApi.getFoodLabels(bitmap, onSuccess, onFailure)
    }

    @WorkerThread
    override suspend fun getFoodNamesFromImage(
        filePath: String,
        onSuccess: (List<String>) -> Unit,
        onFailure: () -> Unit,
    ) {
        FridgeApi.getFoodLabels(filePath, onSuccess, onFailure)
    }
}