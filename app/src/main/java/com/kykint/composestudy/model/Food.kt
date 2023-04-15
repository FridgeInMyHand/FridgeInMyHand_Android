package com.kykint.composestudy.model

data class Food(
    /**
     * Food name
     */
    var name: String,

    /**
     * UNIX Timestamp
     */
    var bestBefore: Long? = null,
)
