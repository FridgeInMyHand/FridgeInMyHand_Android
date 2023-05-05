package com.kykint.composestudy.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.kykint.composestudy.data.Food
import com.kykint.composestudy.repository.IMyFoodListRepository
import com.kykint.composestudy.repository.MyFoodListRepositoryImpl
import com.kykint.composestudy.utils.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * https://medium.com/geekculture/add-remove-in-lazycolumn-list-aka-recyclerview-jetpack-compose-7c4a2464fc9f
 * https://www.charlezz.com/?p=45667
 */

/**
 * ViewModel for FridgeMainActivity
 */
abstract class IFridgeMainViewModel : ViewModel() {
    sealed class UiState {
        object Normal : UiState()

        object Success : UiState()
        // data class Success(
        //     val list: List<Food>
        // ) : State()

        object Failure : UiState()

        object Loading : UiState()
    }

    abstract val uiState: StateFlow<UiState>

    /**
     * 음식 수정 다이얼로그 떠있는지 여부
     */
    sealed class EditingState {
        object Normal : EditingState()
        data class Editing(val foodIndex: Int) : EditingState()
    }

    abstract val editingState: StateFlow<EditingState>

    // abstract val myModels: LiveData<List<MyModel>>
    abstract val foods: LiveData<List<Food>>
    abstract val onItemClickEvent: MutableLiveData<Event<Food>>

    abstract fun refreshFoods()
    abstract fun onItemClick(position: Int)
    abstract fun onBtnClick()
    abstract fun updateFoodProperty(
        position: Int,
        newName: String? = null,
        newBestBefore: Long? = null,
        newAmount: String? = null,
        newPublic: Boolean? = null
    )

    abstract fun deleteFood(position: Int)
    abstract fun editFood(position: Int)
    abstract fun saveEditedFood(position: Int, editedFood: Food)
    abstract fun cancelEditFood()
}

class FridgeMainViewModel(
    private val repository: IMyFoodListRepository,
) : IFridgeMainViewModel() {
    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Normal)
    override val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _editingState: MutableStateFlow<EditingState> =
        MutableStateFlow(EditingState.Normal)
    override val editingState: StateFlow<EditingState> = _editingState.asStateFlow()

    // https://developer.android.com/topic/libraries/architecture/viewmodel/viewmodel-factories#kotlin_1
    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val repo = MyFoodListRepositoryImpl
                return FridgeMainViewModel(repo) as T
            }
        }
    }

    // private val _foods: MutableLiveData<List<MyModel>> = MutableLiveData()
    // override val foods: LiveData<List<MyModel>> = _myModels
    override var foods = MyFoodListRepositoryImpl.foods
        private set

    // override val onItemClickEvent: MutableLiveData<MyModel> = MutableLiveData()
    override val onItemClickEvent: MutableLiveData<Event<Food>> = MutableLiveData()

    override fun refreshFoods() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = UiState.Loading
            repository.fetchFoodList(
                onSuccess = {
                    _uiState.value = UiState.Success
                },
                onFailure = {
                    _uiState.value = UiState.Failure
                },
            )
        }
    }

    override fun onItemClick(position: Int) {
        // _foods.value?.getOrNull(position)?.let {
        // foods.getOrNull(position)?.let {
        // onItemClickEvent.postValue(it)
        // onItemClickEvent.postValue(Event(it))
        // }
    }

    override fun onBtnClick() {
        // foods.add(Food(name = "${++idx}"))
    }

    override fun updateFoodProperty(
        position: Int,
        newName: String?,
        newBestBefore: Long?,
        newAmount: String?,
        newPublic: Boolean?,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = UiState.Loading
            repository.updateFood(
                position,
                newName, newBestBefore, newAmount, newPublic,
                onSuccess = {
                    refreshFoods()
                },
                onFailure = {
                    _uiState.value = UiState.Failure
                },
            )
        }
    }

    override fun editFood(position: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _editingState.value = EditingState.Editing(position)
        }
    }

    override fun saveEditedFood(position: Int, editedFood: Food) {
        viewModelScope.launch(Dispatchers.IO) {
            _editingState.value = EditingState.Normal
            updateFoodProperty(
                position,
                editedFood.name,
                editedFood.bestBefore,
                editedFood.amount,
                editedFood.isPublic,
            )
        }
    }

    override fun cancelEditFood() {
        viewModelScope.launch {
            _editingState.value = EditingState.Normal
        }
    }

    override fun deleteFood(position: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = UiState.Loading
            repository.deleteFood(
                position,
                onSuccess = {
                    refreshFoods()
                },
                onFailure = {
                    _uiState.value = UiState.Failure
                },
            )
        }
    }
}

/**
 * Used for preview
 */
class DummyFridgeMainViewModel : IFridgeMainViewModel() {
    override val uiState: StateFlow<UiState> = MutableStateFlow(UiState.Normal).asStateFlow()
    override val editingState: StateFlow<EditingState> =
        MutableStateFlow(EditingState.Normal).asStateFlow()
    override val foods: MutableLiveData<List<Food>>
        get() = MutableLiveData(
            listOf(
                Food(name = "김치", bestBefore = 1681761600),
                Food(name = "계란", bestBefore = 1689537600),
                Food(name = "감자", bestBefore = 1692129600),
                Food(name = "스팸", bestBefore = 1692302400),
            )
        )
    override val onItemClickEvent: MutableLiveData<Event<Food>>
        get() = MutableLiveData()

    override fun refreshFoods() {}
    override fun onItemClick(position: Int) {}
    override fun onBtnClick() {}
    override fun updateFoodProperty(
        position: Int,
        newName: String?,
        newBestBefore: Long?,
        newAmount: String?,
        newPublic: Boolean?
    ) {
    }

    override fun deleteFood(position: Int) {}
    override fun editFood(position: Int) {}
    override fun saveEditedFood(position: Int, editedFood: Food) {}
    override fun cancelEditFood() {}
}
