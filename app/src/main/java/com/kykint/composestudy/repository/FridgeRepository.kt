package com.kykint.composestudy.repository

import androidx.annotation.WorkerThread
import com.kykint.composestudy.api.FridgeInfoApi
import com.kykint.composestudy.data.Food

interface IFridgeRepository {
    @WorkerThread
    suspend fun getFoods(
        onSuccess: (List<Food>) -> Unit = {},
        onFailure: () -> Unit = {},
    )
}

class FridgeRepositoryImpl : IFridgeRepository {
    @WorkerThread
    override suspend fun getFoods(
        onSuccess: (List<Food>) -> Unit,
        onFailure: () -> Unit
    ) {
        FridgeInfoApi.getFridgeInfo(
            onSuccess = { response ->
                // TODO: Handle failures
                response?.let {
                    onSuccess(response.names.map { Food(it.name, it.bestBefore) })
                }
            }
        )
    }
}

/**
 * Used for testing before backend implementation is done
 */
class DummyFridgeRepositoryImpl : IFridgeRepository {
    @WorkerThread
    override suspend fun getFoods(
        onSuccess: (List<Food>) -> Unit,
        onFailure: () -> Unit
    ) {
        onSuccess(
            listOf(
                Food(name = "김치", bestBefore = 1681761600),
                Food(name = "계란", bestBefore = 1689537600),
                Food(name = "감자", bestBefore = 1692129600),
                Food(name = "스팸", bestBefore = 1692302400),
                Food(name = "소금", bestBefore = 1692302400),
            )
        )
    }
}