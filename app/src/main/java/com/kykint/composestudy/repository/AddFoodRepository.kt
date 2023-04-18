package com.kykint.composestudy.repository

import android.graphics.Bitmap
import com.kykint.composestudy.api.PhotoAnalysis
import com.kykint.composestudy.data.photoanalysis.Response

interface IAddFoodRepository {
    suspend fun getFoodNamesFromImage(
        bitmap: Bitmap,
        onSuccess: (Response?) -> Unit = {},
        onFailure: () -> Unit = {},
    )
}

/**
 * Used for testing before backend implementation is done
 */
class AddFoodRepositoryImpl : IAddFoodRepository {
    override suspend fun getFoodNamesFromImage(
        bitmap: Bitmap,
        onSuccess: (Response?) -> Unit,
        onFailure: () -> Unit,
    ) {
        PhotoAnalysis.getObjectInfos(bitmap, onSuccess, onFailure)
    }
}