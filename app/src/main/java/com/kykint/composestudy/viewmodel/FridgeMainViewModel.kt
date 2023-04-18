package com.kykint.composestudy.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.kykint.composestudy.data.Food
import com.kykint.composestudy.repository.DummyFridgeRepositoryImpl
import com.kykint.composestudy.repository.IFridgeRepository
import com.kykint.composestudy.utils.Event

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
        // TODO: Replace FakeFactory with real one
        val FakeFactory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
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

/**
 * Used for preview
 */
class DummyFridgeMainViewModel : IFridgeMainViewModel() {
    override val foods: List<Food>
        get() = listOf(
            Food(name = "김치", bestBefore = 1681761600),
            Food(name = "계란", bestBefore = 1689537600),
            Food(name = "감자", bestBefore = 1692129600),
            Food(name = "스팸", bestBefore = 1692302400),
        )
    override val onItemClickEvent: MutableLiveData<Event<Food>>
        get() = MutableLiveData()

    override fun loadFoods() {}
    override fun onItemClick(position: Int) {}
    override fun onBtnClick() {}
}
