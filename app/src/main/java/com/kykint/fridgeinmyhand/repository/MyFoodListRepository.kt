package com.kykint.fridgeinmyhand.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kykint.fridgeinmyhand.api.FridgeApi
import com.kykint.fridgeinmyhand.data.Food

interface IMyFoodListRepository {
    val foods: LiveData<List<Food>>

    suspend fun fetchFoodList(
        onSuccess: () -> Unit = {},
        onFailure: () -> Unit = {},
    )

    @WorkerThread
    suspend fun addToFoodList(
        newFoods: List<Food>,
        onSuccess: () -> Unit = {},
        onFailure: () -> Unit = {},
    )

    @WorkerThread
    suspend fun saveFoodList(
        onSuccess: () -> Unit = {},
        onFailure: () -> Unit = {},
    )

    @WorkerThread
    suspend fun updateFood(
        index: Int,
        newName: String?,
        newBestBefore: Long?,
        newAmount: String?,
        newPublic: Boolean?,
        onSuccess: () -> Unit = {},
        onFailure: () -> Unit = {},
    )

    @WorkerThread
    suspend fun deleteFood(
        index: Int,
        onSuccess: () -> Unit = {},
        onFailure: () -> Unit = {},
    )
}

object MyFoodListRepositoryImpl : IMyFoodListRepository {
    private val _foods: MutableLiveData<List<Food>> = MutableLiveData()
    override val foods: LiveData<List<Food>>
        get() = _foods

    @WorkerThread
    override suspend fun fetchFoodList(
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    ) {
        FridgeApi.getMyFoodList(
            onSuccess = { response ->
                // TODO: Handle failures
                _foods.value = response.names.map {
                    Food(
                        it.name, it.bestBefore, it.amount, it.isPublic
                    )
                }
                onSuccess()
            },
            onFailure = onFailure
        )
    }

    /**
     * 기존 음식 목록에 foods 목록을 추가하여 서버에 저장
     */
    @WorkerThread
    override suspend fun addToFoodList(
        newFoods: List<Food>,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    ) {
        FridgeApi.saveFoodList(
            _foods.value?.plus(newFoods) ?: newFoods,
            onSuccess = onSuccess,
            onFailure = onFailure,
        )
    }

    /**
     * 기존 음식 목록 그대로 서버에 저장
     */
    @WorkerThread
    override suspend fun saveFoodList(
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    ) {
        FridgeApi.saveFoodList(
            _foods.value ?: emptyList(),
            onSuccess = onSuccess,
            onFailure = onFailure,
        )
    }

    /**
     * 음식 아이템 정보 업데이트
     */
    @WorkerThread
    override suspend fun updateFood(
        index: Int,
        newName: String?,
        newBestBefore: Long?,
        newAmount: String?,
        newPublic: Boolean?,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    ) {
        if (_foods.value == null || _foods.value!!.size <= index) {
            onFailure()
            return
        }

        val oldFood = _foods.value!![index]
        val newFood = oldFood.copy(
            name = newName ?: oldFood.name,
            bestBefore = newBestBefore ?: oldFood.bestBefore,
            amount = newAmount ?: oldFood.amount,
            isPublic = newPublic ?: oldFood.isPublic,
        )

        val newList = _foods.value!!.toMutableList()
        newList[index] = newFood
        FridgeApi.saveFoodList(
            newList,
            onSuccess = onSuccess,
            onFailure = onFailure,
        )
    }

    /**
     * 음식 삭제
     */
    @WorkerThread
    override suspend fun deleteFood(
        index: Int,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    ) {
        if (_foods.value == null || _foods.value!!.size <= index) {
            onFailure()
            return
        }

        val newList = _foods.value!!.toMutableList()
        newList.removeAt(index)
        FridgeApi.saveFoodList(
            newList,
            onSuccess = onSuccess,
            onFailure = onFailure,
        )
    }
}

/**
 * Used for testing before backend implementation is done
 */
/*
class DummyMyFoodListRepositoryImpl : IMyFoodListRepository {
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
*/