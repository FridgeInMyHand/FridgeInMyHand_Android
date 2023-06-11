package com.kykint.fridgeinmyhand.viewmodel

import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.kykint.fridgeinmyhand.App
import com.kykint.fridgeinmyhand.data.Food
import com.kykint.fridgeinmyhand.repository.AddFoodRepositoryImpl
import com.kykint.fridgeinmyhand.repository.FoodListRepositoryImpl
import com.kykint.fridgeinmyhand.repository.IAddFoodRepository
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
    abstract val items: SnapshotStateList<Food>

    abstract fun onAddFoodItemClicked()
    abstract fun onPictureTaken(path: String)
    abstract fun addItem()
    abstract fun removeItem(index: Int)
    abstract fun changeItemName(index: Int, newName: String)
    abstract fun changeItemBestBefore(index: Int, newBestBefore: Long)
    abstract fun changeItemAmount(index: Int, newAmount: String)
    abstract fun changeItemPublic(index: Int, newPublic: Boolean)
    abstract fun addFoodDone(
        onSuccess: () -> Unit = {},
        onFailure: () -> Unit = {},
    )
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
                onSuccess = { names ->
                    if (names.isEmpty()) {
                        Toast.makeText(
                            App.context,
                            "감지된 음식이 없습니다! 보다 가까이서 촬영해보세요.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            App.context,
                            "새 음식을 추가했습니다!\n\"${names.joinToString("\", \"")}\"",
                            Toast.LENGTH_SHORT
                        ).show()
                        names.map { Food(name = it) }.let { items.addAll(0, it) }
                    }
                    _state.value = State.Success
                },
                onFailure = {
                    _state.value = State.Failure
                }
            )
        }
    }

    override fun addItem() {
        items.add(Food(name = ""))
    }

    override fun removeItem(index: Int) {
        items.removeAt(index)
    }

    override fun changeItemName(index: Int, newName: String) {
        // items[index] = items[index].copy(name = newName)
        items[index].name = newName
    }

    override fun changeItemBestBefore(index: Int, newBestBefore: Long) {
        // items[index] = items[index].copy(bestBefore = newBestBefore)
        items[index].bestBefore = newBestBefore
    }

    override fun changeItemAmount(index: Int, newAmount: String) {
        items[index].amount = newAmount
    }

    override fun changeItemPublic(index: Int, newPublic: Boolean) {
        items[index].publicFood = newPublic
    }

    override fun addFoodDone(
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    ) {
        viewModelScope.launch {
            _state.value = State.Loading
            FoodListRepositoryImpl.addToMyFoodList(
                items,
                onSuccess = {
                    _state.value = State.Success
                    onSuccess()
                },
                onFailure = {
                    _state.value = State.Failure
                    onFailure()
                }
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
    override val state: StateFlow<State> = MutableStateFlow(State.Normal).asStateFlow()

    // private val _detectedFoodNames = MutableLiveData<List<String>>()
    // override val detectedFoodNames = _detectedFoodNames
    override val items = mutableStateListOf(
        Food(name = "김치"),
        Food(name = "시금치"),
        Food(name = "계란"),
        Food(name = "스팸"),
    )

    override fun onAddFoodItemClicked() {}
    override fun onPictureTaken(path: String) {}
    override fun addItem() {}
    override fun removeItem(index: Int) {}
    override fun changeItemName(index: Int, newName: String) {}
    override fun changeItemBestBefore(index: Int, newBestBefore: Long) {}
    override fun changeItemAmount(index: Int, newAmount: String) {}
    override fun changeItemPublic(index: Int, newPublic: Boolean) {}
    override fun addFoodDone(
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    ) {
    }
}