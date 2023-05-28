package com.kykint.fridgeinmyhand.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kykint.fridgeinmyhand.data.Food

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

/**
 * 사용자 위치 등록 Request
 *
 * POST /userLocation
 */
data class PostUserLocationModel(
    @SerializedName("UUID")
    val uuid: String,

    @SerializedName("lat")
    val lat: Double,

    @SerializedName("long")
    val long: Double,
)

/**
 * 사용자 카카오톡 오픈채팅 링크 등록 Request
 *
 * POST /userURL
 */
data class PostUserUrlModel(
    @SerializedName("UUID")
    val uuid: String,

    @SerializedName("url")
    val url: String,
)
