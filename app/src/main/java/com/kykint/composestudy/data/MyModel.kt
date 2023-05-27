package com.kykint.composestudy.data

import java.util.UUID

data class MyModel(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
)