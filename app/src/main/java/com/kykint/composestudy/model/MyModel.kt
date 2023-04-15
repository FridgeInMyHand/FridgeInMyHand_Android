package com.kykint.composestudy.model

import java.util.*

data class MyModel(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
)