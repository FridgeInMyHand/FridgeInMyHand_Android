package com.kykint.composestudy.viewmodel

import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.kykint.composestudy.data.Food
import com.kykint.composestudy.data.photoanalysis.Response
import com.kykint.composestudy.repository.AddFoodRepositoryImpl
import com.kykint.composestudy.repository.IAddFoodRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class IAddFoodViewModel : ViewModel() {
    abstract val detectedFoodNames: LiveData<List<String>>
    abstract val items: List<Food>

    abstract fun onAddFoodItemClicked(): Unit
    abstract fun onPictureTaken(bitmap: Bitmap): Unit
}

class AddFoodViewModel(
    private val repository: IAddFoodRepository
) : IAddFoodViewModel() {
    private val _detectedFoodNames = MutableLiveData<List<String>>()
    override val detectedFoodNames = _detectedFoodNames

    override val items = mutableStateListOf<Food>(
        Food(name = "김치"),
        Food(name = "시금치"),
        Food(name = "계란"),
        Food(name = "스팸"),
    )

    override fun onAddFoodItemClicked() {
        items.add(Food(name = "새 음식"))
    }

    override fun onPictureTaken(bitmap: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getFoodNamesFromImage(
                bitmap,
                { response: Response? ->
                    response?.names?.let { names ->
                        _detectedFoodNames.postValue(
                            names.map { it.name }
                        )
                    }
                }
            )
        }
    }

    fun test() {
        viewModelScope.launch(Dispatchers.IO) {
            _detectedFoodNames.postValue(
                listOf("음식1", "음식2")
            )
        }
    }

    // https://developer.android.com/topic/libraries/architecture/viewmodel/viewmodel-factories#kotlin_1
    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val repo = AddFoodRepositoryImpl()
                return AddFoodViewModel(repo) as T
            }
        }
    }
}

class DummyAddFoodViewModel : IAddFoodViewModel() {
    private val _detectedFoodNames = MutableLiveData<List<String>>()
    override val detectedFoodNames = _detectedFoodNames
    override val items = listOf(
        Food(name = "김치"),
        Food(name = "시금치"),
        Food(name = "계란"),
        Food(name = "스팸"),
    )

    override fun onAddFoodItemClicked() {}
    override fun onPictureTaken(bitmap: Bitmap) {}
}