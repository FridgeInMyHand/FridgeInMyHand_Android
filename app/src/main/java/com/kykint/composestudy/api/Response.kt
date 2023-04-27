package com.kykint.composestudy.api

import com.google.gson.annotations.SerializedName

abstract class Response

data class PhotoAnalysisResponse(
    @SerializedName("names") val names: ArrayList<Content>
) : Response() {

    data class Content(
        @SerializedName("name")
        val name: String,
        @SerializedName("bestBefore")
        val bestBefore: Long, // TODO: check null safety
    )
}

data class FridgeInfoResponse(
    @SerializedName("names") val names: ArrayList<Content>
) : Response() {

    data class Content(
        @SerializedName("name")
        val name: String,
        @SerializedName("bestBefore")
        val bestBefore: Long, // TODO: check null safety
    )
}