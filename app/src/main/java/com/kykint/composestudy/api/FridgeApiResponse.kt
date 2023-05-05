package com.kykint.composestudy.api

import com.google.gson.annotations.SerializedName

interface BaseResponse

/**
 * 음식 목록 응답
 *
 * GET /food
 */
data class FoodListResponse(
    @SerializedName("names") val names: ArrayList<Content>
) : BaseResponse {

    data class Content(
        @SerializedName("name")
        val name: String,

        @SerializedName("amount")
        val amount: String?,

        @SerializedName("bestBefore")
        val bestBefore: Long?,

        @SerializedName("public")
        val isPublic: Boolean,
    )
}

/**
 * 음식 레이블 식별 응답
 *
 * POST /detect
 */
class FoodClassificationResponse
    : ArrayList<FoodClassificationResponse.Content>(), BaseResponse {

    data class Content(
        @SerializedName("idx")
        val idx: Int,

        @SerializedName("data")
        val data: String,
    )
}