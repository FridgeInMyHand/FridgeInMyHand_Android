package com.kykint.composestudy.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kykint.composestudy.data.Food

/**
 * 음식 목록 등록 Request
 *
 * POST /food
 */
data class PostFoodListModel(
    @SerializedName("UUID")
    val uuid: String,

    @SerializedName("names")
    var names: List<Data> = emptyList(),
) {
    data class Data(
        @SerializedName("name")
        val name: String,

        @SerializedName("amount")
        @Expose
        val amount: String?,

        @SerializedName("bestBefore")
        @Expose
        val bestBefore: Long?,

        @SerializedName("public")
        val public: Boolean
    ) {
        companion object {
            fun fromFood(food: Food) =
                Data(
                    food.name,
                    food.amount,
                    food.bestBefore,
                    food.isPublic,
                )
        }
    }

    fun fromFoods(foods: List<Food>): PostFoodListModel {
        names = foods.map { Data.fromFood(it) }
        return this
    }
}