package com.kykint.fridgeinmyhand.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kykint.fridgeinmyhand.data.Food

/**
 * 음식 목록 받아오기 Request Body
 *
 * GET /food
 */
data class GetFoodRequestModel(
    @SerializedName("requestUUID")
    val requestUUID: String,

    @SerializedName("userUUID")
    val userUUID: String,
)

/**
 * 음식 목록 서버에 저장 Request Body
 *
 * POST /food
 */
data class PostFoodRequestModel(
    @SerializedName("userUUID")
    val userUUID: String,

    @SerializedName("names")
    var names: List<Data> = emptyList(),
) {
    data class Data(
        @SerializedName("foodName")
        val foodName: String,

        @SerializedName("amount")
        val amount: String = "",

        @SerializedName("bestBefore")
        @Expose
        val bestBefore: Long?,

        @SerializedName("publicFood")
        @Expose
        val publicFood: Boolean,
    ) {
        companion object {
            fun fromFood(food: Food) =
                Data(
                    food.name,
                    food.amount,
                    food.bestBefore,
                    food.publicFood ?: false,
                )
        }
    }

    fun fromFoods(foods: List<Food>): PostFoodRequestModel {
        names = foods.map { Data.fromFood(it) }
        return this
    }
}

/**
 * 사용자 정보 받아오기 Request Body
 *
 * GET /user
 */
data class GetUserRequestModel(
    @SerializedName("UUID")
    val UUID: String,
)

/**
 * 사용자 위치 등록 Request Body
 *
 * POST /userLocation
 */
data class PostUserLocationRequestModel(
    @SerializedName("UUID")
    val UUID: String,

    @SerializedName("lat")
    val lat: Double,

    @SerializedName("long")
    val long: Double,
)

/**
 * 사용자 카카오톡 링크 등록 Request Body
 *
 * POST /userURL
 */
data class PostUserUrlRequestModel(
    @SerializedName("UUID")
    val UUID: String,

    @SerializedName("url")
    val url: String,
)

/**
 * 근처 사용자 정보 받아오기 Request Body
 *
 * GET /nearbyUser
 */
data class GetNearbyUserRequestModel(
    @SerializedName("UUID")
    val UUID: String,

    @SerializedName("lat")
    val lat: Double,

    @SerializedName("long")
    val long: Double,

    @SerializedName("lat_limit")
    val lat_limit: Double,

    @SerializedName("long_limit")
    val long_limit: Double,
)
