package com.kykint.fridgeinmyhand.repository

import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.WorkerThread
import com.kykint.fridgeinmyhand.api.AiApi
import com.kykint.fridgeinmyhand.data.Food
import java.io.File

interface IAddFoodRepository {
    @WorkerThread
    suspend fun getFoodInfosFromImage(
        bitmap: Bitmap,
        onSuccess: (List<Food>) -> Unit = {},
        onFailure: () -> Unit = {},
    )

    @WorkerThread
    suspend fun getFoodInfosFromImage(
        filePath: String,
        onSuccess: (List<Food>) -> Unit = {},
        onFailure: () -> Unit = {},
    )

    @WorkerThread
    suspend fun getFoodInfosFromImage(
        file: File,
        onSuccess: (List<Food>) -> Unit = {},
        onFailure: () -> Unit = {},
    )
}

/**
 * Used for testing before backend implementation is done
 */
class AddFoodRepositoryImpl : IAddFoodRepository {
    @RequiresApi(Build.VERSION_CODES.O)
    @WorkerThread
    override suspend fun getFoodInfosFromImage(
        bitmap: Bitmap,
        onSuccess: (List<Food>) -> Unit,
        onFailure: () -> Unit,
    ) {
        AiApi.getFoodInfos(bitmap, onSuccess, onFailure)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @WorkerThread
    override suspend fun getFoodInfosFromImage(
        filePath: String,
        onSuccess: (List<Food>) -> Unit,
        onFailure: () -> Unit,
    ) {
        AiApi.getFoodInfos(filePath, onSuccess, onFailure)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @WorkerThread
    override suspend fun getFoodInfosFromImage(
        file: File,
        onSuccess: (List<Food>) -> Unit,
        onFailure: () -> Unit,
    ) {
        AiApi.getFoodInfos(file, onSuccess, onFailure)
    }
}