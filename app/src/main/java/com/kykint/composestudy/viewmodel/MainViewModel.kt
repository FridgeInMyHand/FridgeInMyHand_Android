package com.kykint.composestudy.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.kykint.composestudy.model.MyModel
import com.kykint.composestudy.utils.Event

/**
 * https://medium.com/geekculture/add-remove-in-lazycolumn-list-aka-recyclerview-jetpack-compose-7c4a2464fc9f
 * https://www.charlezz.com/?p=45667
 */

/**
 * ViewModel for MainActivity
 */
abstract class IMainViewModel : ViewModel() {
    // abstract val myModels: LiveData<List<MyModel>>
    abstract val myModels: List<MyModel>
    abstract val onItemClickEvent: MutableLiveData<Event<MyModel>>
    // abstract val startFridgeMainActivity: SingleLiveEvent<Any>

    abstract fun loadMyModels()
    abstract fun onItemClick(position: Int)
    // abstract fun onBtnClick()
}

class MainViewModel(
    private val repository: IMyRepository,
) : IMainViewModel() {

    // https://developer.android.com/topic/libraries/architecture/viewmodel/viewmodel-factories#kotlin_1
    companion object {
        val FakeFactory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val repo = FakeMyRepositoryImpl()
                return MainViewModel(repo) as T
            }
        }
    }

    var idx = 0

    // private val _myModels: MutableLiveData<List<MyModel>> = MutableLiveData()
    // override val myModels: LiveData<List<MyModel>> = _myModels
    override var myModels = mutableStateListOf<MyModel>()
        private set

    // override val onItemClickEvent: MutableLiveData<MyModel> = MutableLiveData()
    override val onItemClickEvent = MutableLiveData<Event<MyModel>>()

    // override val startFridgeMainActivity = SingleLiveEvent<Any>()

    override fun loadMyModels() {
        repository.getModels().let {
            // _myModels.postValue(it) // TODO: postValue 를 쓴 이유?
            myModels.addAll(it)
        }
    }

    override fun onItemClick(position: Int) {
        // _myModels.value?.getOrNull(position)?.let {
        myModels.getOrNull(position)?.let {
            // onItemClickEvent.postValue(it)
            onItemClickEvent.postValue(Event(it))
        }
    }

    /*
    override fun onBtnClick() {
        myModels.add(MyModel(title = "${++idx}"))
        startFridgeMainActivity.call()
    }
    */
}

/*
class MainViewModelFactory(
    val repository: IMyRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class :: ${modelClass::class.java.simpleName}")
    }
}
*/

interface IMyRepository {
    fun getModels(): List<MyModel>
}

class FakeMainViewModel : IMainViewModel() {
    override val myModels =
        (1..5).map { i -> MyModel(title = "Fake title $i") }
    override val onItemClickEvent = MutableLiveData<Event<MyModel>>()
    // override val startFridgeMainActivity = SingleLiveEvent<Any>()

    override fun loadMyModels() {}
    override fun onItemClick(position: Int) {}
    // override fun onBtnClick() {}
}

class FakeMyRepositoryImpl : IMyRepository {
    override fun getModels(): List<MyModel> {
        return (1..5)
            .map { i -> MyModel(title = "Fake title $i") }
            .toList()
    }
}