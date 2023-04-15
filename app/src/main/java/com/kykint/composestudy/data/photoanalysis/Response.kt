package com.kykint.composestudy.data.photoanalysis

import com.google.gson.annotations.SerializedName

data class Response(@SerializedName("names") val names: ArrayList<Content>)

data class Content(
    @SerializedName("name")
    val name: String,
    @SerializedName("bestBefore")
    val bestBefore: Long, // TODO: check null safety
)