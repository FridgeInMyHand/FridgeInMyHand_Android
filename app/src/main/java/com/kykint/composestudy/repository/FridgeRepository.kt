package com.kykint.composestudy.repository

import com.kykint.composestudy.data.Food

interface IFridgeRepository {
    fun getFoods(): List<Food>
}

/**
 * Used for testing before backend implementation is done
 */
class DummyFridgeRepositoryImpl : IFridgeRepository {
    override fun getFoods(): List<Food> {
        return listOf(
            Food(name = "김치", bestBefore = 1681761600),
            Food(name = "계란", bestBefore = 1689537600),
            Food(name = "감자", bestBefore = 1692129600),
            Food(name = "스팸", bestBefore = 1692302400),
            Food(name = "소금", bestBefore = 1692302400),
        )
    }
}