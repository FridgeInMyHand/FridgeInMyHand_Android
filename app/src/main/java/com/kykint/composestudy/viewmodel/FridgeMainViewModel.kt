package com.kykint.composestudy.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.kykint.composestudy.model.Food
import com.kykint.composestudy.model.MyModel
import com.kykint.composestudy.utils.Event
import com.kykint.composestudy.utils.SingleLiveEvent
import com.kykint.composestudy.utils.writeImageToSdcard

/**
 * https://medium.com/geekculture/add-remove-in-lazycolumn-list-aka-recyclerview-jetpack-compose-7c4a2464fc9f
 * https://www.charlezz.com/?p=45667
 */

/**
 * ViewModel for FridgeMainActivity
 */
abstract class IFridgeMainViewModel : ViewModel() {
    // abstract val myModels: LiveData<List<MyModel>>
    abstract val foods: List<Food>
    abstract val onItemClickEvent: MutableLiveData<Event<Food>>

    abstract fun loadFoods()
    abstract fun onItemClick(position: Int)
    abstract fun onBtnClick()
}

class FridgeMainViewModel(
    private val repository: IFridgeRepository,
) : IFridgeMainViewModel() {

    // https://developer.android.com/topic/libraries/architecture/viewmodel/viewmodel-factories#kotlin_1
    companion object {
        val FakeFactory: ViewModelProvider.Factory = object: ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val repo = DummyFridgeRepositoryImpl()
                return FridgeMainViewModel(repo) as T
            }
        }
    }

    var idx = 0

    // private val _foods: MutableLiveData<List<MyModel>> = MutableLiveData()
    // override val foods: LiveData<List<MyModel>> = _myModels
    override var foods = mutableStateListOf<Food>()
        private set

    // override val onItemClickEvent: MutableLiveData<MyModel> = MutableLiveData()
    override val onItemClickEvent: MutableLiveData<Event<Food>> = MutableLiveData()

    override fun loadFoods() {
        repository.getFoods().let {
            // _myModels.postValue(it) // TODO: postValue 를 쓴 이유?
            foods.addAll(it)
        }
    }

    override fun onItemClick(position: Int) {
        // _foods.value?.getOrNull(position)?.let {
        foods.getOrNull(position)?.let {
            // onItemClickEvent.postValue(it)
            onItemClickEvent.postValue(Event(it))
        }
    }

    override fun onBtnClick() {
        foods.add(Food(name = "${++idx}"))
    }
}

class FridgeMainViewModelFactory(
    val repository: IFridgeRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FridgeMainViewModel::class.java)) {
            return FridgeMainViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class :: ${modelClass::class.java.simpleName}")
    }
}

interface IFridgeRepository {
    fun getFoods(): List<Food>
}

/**
 * Dummy codes for testing
 */
class DummyFridgeMainViewModel : IFridgeMainViewModel() {
    override val foods: List<Food>
        get() {
            // return MutableLiveData(list)
            return (1..5)
                .map { i -> Food(name = "Food $i") }
        }
    override val onItemClickEvent: MutableLiveData<Event<Food>>
        get() = MutableLiveData()

    override fun loadFoods() {}
    override fun onItemClick(position: Int) {}
    override fun onBtnClick() {}
}

class DummyFridgeRepositoryImpl : IFridgeRepository {
    override fun getFoods(): List<Food> {
        return (1..5)
            .map { i -> Food(name = "Dummy food $i") }
            .toList()
    }
}