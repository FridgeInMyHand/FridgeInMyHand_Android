package com.kykint.composestudy.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.kykint.composestudy.model.Food

abstract class IAddFoodViewModel : ViewModel() {
    abstract val items: List<Food>

    abstract fun onAddFoodItemClicked(): Unit
}

class AddFoodViewModel : IAddFoodViewModel() {
    override val items = mutableStateListOf<Food>(
        Food(name = "김치"),
        Food(name = "시금치"),
        Food(name = "계란"),
        Food(name = "스팸"),
    )

    override fun onAddFoodItemClicked() {
        items.add(Food(name = "새 음식"))
    }
}

class DummyAddFoodViewModel : IAddFoodViewModel() {
    override val items = listOf(
        Food(name = "김치"),
        Food(name = "시금치"),
        Food(name = "계란"),
        Food(name = "스팸"),
    )

    override fun onAddFoodItemClicked() {

    }
}