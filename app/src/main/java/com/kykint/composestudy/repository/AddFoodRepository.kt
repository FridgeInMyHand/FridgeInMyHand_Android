package com.kykint.composestudy.repository

import android.graphics.Bitmap
import androidx.annotation.WorkerThread
import com.kykint.composestudy.api.PhotoAnalysisApi
import com.kykint.composestudy.api.PhotoAnalysisResponse

interface IAddFoodRepository {
    @WorkerThread
    suspend fun getFoodNamesFromImage(
        bitmap: Bitmap,
        // TODO: Dependency 제거 필요. PhotoAnalysisResponse 대신 String 등 보다 Generic한 Type 요구.
        onSuccess: (PhotoAnalysisResponse?) -> Unit = {},
        onFailure: () -> Unit = {},
    )

    @WorkerThread
    suspend fun getFoodNamesFromImage(
        filePath: String,
        onSuccess: (PhotoAnalysisResponse?) -> Unit = {},
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
        onSuccess: (PhotoAnalysisResponse?) -> Unit,
        onFailure: () -> Unit,
    ) {
        PhotoAnalysisApi.getObjectInfos(bitmap, onSuccess, onFailure)
    }

    @WorkerThread
    override suspend fun getFoodNamesFromImage(
        filePath: String,
        onSuccess: (PhotoAnalysisResponse?) -> Unit,
        onFailure: () -> Unit,
    ) {
        PhotoAnalysisApi.getObjectInfos(filePath, onSuccess, onFailure)
    }
}