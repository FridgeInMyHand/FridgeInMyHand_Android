package com.kykint.composestudy.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.kykint.composestudy.data.Food
import com.kykint.composestudy.repository.AddFoodRepositoryImpl
import com.kykint.composestudy.repository.IAddFoodRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

abstract class IAddFoodViewModel : ViewModel() {
    // https://medium.com/@laco2951/android-ui-state-modeling-%EC%96%B4%EB%96%A4%EA%B2%8C-%EC%A2%8B%EC%9D%84%EA%B9%8C-7b6232543f25
    sealed class State {
        object Normal : State()

        // 서버에서 사진 분석 요청이 끝났을 경우
        object Success : State()
        // data class Success(
        //     val list: List<Food>
        // ) : State()

        object Failure : State()

        object Loading : State()
    }

    abstract val state: StateFlow<State>

    // abstract val detectedFoodNames: LiveData<List<String>>
    abstract val items: List<Food>

    abstract fun onAddFoodItemClicked(): Unit
    abstract fun onPictureTaken(path: String): Unit
}

class AddFoodViewModel(
    private val repository: IAddFoodRepository
) : IAddFoodViewModel() {
    private val _state: MutableStateFlow<State> = MutableStateFlow(State.Normal)
    override val state: StateFlow<State> = _state.asStateFlow()

    // private val _detectedFoodNames = MutableLiveData<List<String>>()
    // override val detectedFoodNames = _detectedFoodNames

    override val items = mutableStateListOf<Food>(
        Food(name = "김치"),
        Food(name = "시금치"),
        Food(name = "계란"),
        Food(name = "스팸"),
    )

    override fun onAddFoodItemClicked() {
        items.add(Food(name = "새 음식"))
    }

    override fun onPictureTaken(path: String) {
        viewModelScope.launch {
            _state.value = State.Loading
            repository.getFoodNamesFromImage(
                path,
                onSuccess = { response ->
                    response?.let {
                        it.names.map {
                            Food(name = it.name, bestBefore = it.bestBefore)
                        }.let { items.addAll(it) }
                    }
                    _state.value = State.Success
                },
            )
            // TODO: Fail when no response
        }
        //     viewModelScope.launch(Dispatchers.IO) {
        //         repository.getFoodNamesFromImage(
        //             bitmap,
        //             { response: Response? ->
        //                 response?.names?.let { names ->
        //                     _detectedFoodNames.postValue(
        //                         names.map { it.name }
        //                     )
        //                 }
        //             }
        //         )
        //     }
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
    override val state: StateFlow<State> = MutableStateFlow(State.Normal).asStateFlow()

    // private val _detectedFoodNames = MutableLiveData<List<String>>()
    // override val detectedFoodNames = _detectedFoodNames
    override val items = listOf(
        Food(name = "김치"),
        Food(name = "시금치"),
        Food(name = "계란"),
        Food(name = "스팸"),
    )

    override fun onAddFoodItemClicked() {}
    override fun onPictureTaken(path: String) {}
}